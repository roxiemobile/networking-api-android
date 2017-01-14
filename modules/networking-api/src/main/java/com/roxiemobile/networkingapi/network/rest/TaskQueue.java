package com.roxiemobile.networkingapi.network.rest;

import android.support.annotation.NonNull;

import com.roxiemobile.androidcommons.concurrent.MainThreadExecutor;
import com.roxiemobile.androidcommons.concurrent.ParallelWorkerThreadExecutor;
import com.roxiemobile.androidcommons.concurrent.ThreadUtils;
import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.networkingapi.network.http.util.LinkedMultiValueMap;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.RestApiError;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class TaskQueue
{
// MARK: - Construction

    private TaskQueue() {
        // Do nothing
    }

// MARK: - Methods

    /**
     * TODO
     */
    public static @NonNull <Ti, To> Cancellable enqueue(@NonNull Task<Ti, To> task) {
        return enqueue(task, null);
    }

    /**
     * TODO
     */
    public static @NonNull <Ti, To> Cancellable enqueue(@NonNull Task<Ti, To> task, Callback<Ti, To> callback) {
        return enqueue(task, callback, ThreadUtils.runningOnUiThread());
    }

    /**
     * TODO
     */
    public static @NonNull <Ti, To> Cancellable enqueue(@NonNull Task<Ti, To> task, Callback<Ti, To> callback, boolean callbackOnUiThread) {

        // Create new cancellable task
        final InnerFutureTask futureTask = new InnerFutureTask<>(new InnerRunnableTask<>(task, callback, callbackOnUiThread));
        synchronized (sLock) {
            sTasks.add(task.tag(), futureTask);
        }

        // Execute the FutureTask on the background thread
        ParallelWorkerThreadExecutor.instance().execute(futureTask);

        // Done
        return futureTask;
    }

    // FIXME: Code refactoring needed
    public static void cancel(String tag) {
        List<Cancellable> cancellableTasks;

        synchronized (sLock) {
            cancellableTasks = sTasks.remove(tag);
        }

        if (cancellableTasks != null) {
            for (Cancellable task : cancellableTasks) {
                task.cancel();
            }
        }
    }

// MARK: - Inner Types

    private static final class InnerFutureTask<Ti, To> extends FutureTask<Void>
            implements Cancellable
    {
        public InnerFutureTask(@NonNull InnerRunnableTask<Ti, To> runnableTask) {
            super(runnableTask, null);

            // Init instance variables
            mRunnableTask = runnableTask;
        }

        @Override
        protected void done() {
            super.done();

            // Remove the completed task
            synchronized (sLock) {
                List<Cancellable> tasks = sTasks.get(mRunnableTask.mTask.tag());

                if (tasks != null) {
                    tasks.remove(this);
                }
            }
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            mRunnableTask.cancel();
            return super.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean cancel() {
            return cancel(true);
        }

        private final InnerRunnableTask<Ti, To> mRunnableTask;
    }

    private static final class InnerRunnableTask<Ti, To>
            implements Runnable, Cancellable
    {
        public InnerRunnableTask(@NonNull Task<Ti, To> task, Callback<Ti, To> callback, boolean callbackOnUiThread) {
            // Init instance variables
            mTask = task.clone();
            mCallback = (callback != null) ? new InnerCallback<>(callback, callbackOnUiThread) : null;
        }

        @Override
        public void run() {
            mTask.execute(mCallback);
        }

        @Override
        public boolean cancel() {
            return mCallback.cancel(mTask);
        }

        private final Task<Ti, To> mTask;
        private final InnerCallback<Ti, To> mCallback;
    }

    private static final class InnerCallback<Ti, To> extends CallbackDecorator<Ti, To>
    {
        private InnerCallback(@NonNull Callback<Ti, To> callback, boolean callbackOnUiThread) {
            super(callback);

            // Init instance variables
            mExecutor = callbackOnUiThread ? MainThreadExecutor.instance() : ParallelWorkerThreadExecutor.instance();
        }

        @Override
        public boolean onShouldExecute(Call<Ti> call) {
            return !mDone.get() && awaitDone(mExecutor.submit(() -> InnerCallback.super.onShouldExecute(call)), Boolean.FALSE);
        }

        @Override
        public void onResponse(Call<Ti> call, ResponseEntity<To> entity) {
            if (!mDone.getAndSet(true)) {
                mExecutor.execute(() -> super.onResponse(call, entity));
            }
        }

        @Override
        public void onFailure(Call<Ti> call, RestApiError error) {
            if (!mDone.getAndSet(true)) {
                mExecutor.execute(() -> super.onFailure(call, error));
            }
        }

        @Override
        public void onCancel(Call<Ti> call) {
            if (!mDone.getAndSet(true)) {
                mExecutor.execute(() -> super.onCancel(call));
            }
        }

        private <T> T awaitDone(Future<T> future, T defaultValue) {
            T result = defaultValue;
            try {
                // Waits for the computation to complete
                result = future.get();
            }
            catch (ExecutionException | InterruptedException e) {
                Logger.w(TAG, e);
            }
            return result;
        }

        private boolean cancel(Call<Ti> call) {
            boolean result = !mDone.getAndSet(true);

            // Cancel the supplied task
            if (result) {
                try {
                    ((Cancellable) call).cancel();
                }
                catch (ClassCastException e) {
                    Logger.w(TAG, e);
                }
                awaitDone(mExecutor.submit(() -> super.onCancel(call)), null);
            }
            return result;
        }

        private final ExecutorService mExecutor;
        private final AtomicBoolean mDone = new AtomicBoolean(false);
    }

// MARK: - Constants

    private static final String TAG = TaskQueue.class.getSimpleName();

// MARK: - Variables

    private static final LinkedMultiValueMap<String, Cancellable> sTasks = new LinkedMultiValueMap<>();
    private static final Object sLock = new Object();

}
