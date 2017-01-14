package com.roxiemobile.networkingapi.network.rest;

import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.roxiemobile.androidcommons.concurrent.MainThreadExecutor;
import com.roxiemobile.androidcommons.concurrent.ParallelWorkerThreadExecutor;
import com.roxiemobile.androidcommons.concurrent.ThreadUtils;
import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.networkingapi.network.http.util.LinkedMultiValueMap;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.RestApiError;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
        synchronized (sInnerLock) {
            sTasks.add(task.tag(), futureTask);
        }

        // Execute the FutureTask on the background thread
        ParallelWorkerThreadExecutor.instance().execute(futureTask);

        // Done
        return futureTask;
    }

    /**
     * TODO
     */
    public static void cancel(String tag) {
        List<Cancellable> cancellableTasks;

        synchronized (sInnerLock) {
            cancellableTasks = sTasks.remove(tag);
        }

        if (cancellableTasks != null) {
            Stream.of(cancellableTasks).forEach(Cancellable::cancel);
        }
    }

// MARK: - Inner Types

    private static final class InnerFutureTask<Ti, To> extends FutureTask<Void> implements Cancellable
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
            synchronized (sInnerLock) {
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

    private static final class InnerRunnableTask<Ti, To> implements Runnable, Cancellable
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
            mExecutor = callbackOnUiThread ? MainThreadExecutor.instance() : InnerParallelWorkerThreadExecutor.instance();
        }

        @Override
        public boolean onShouldExecute(Call<Ti> call) {
            return !mDone.get() && awaitDone(mExecutor.submit(() -> InnerCallback.super.onShouldExecute(call)), Boolean.FALSE);
        }

        @Override
        public void onSuccess(Call<Ti> call, ResponseEntity<To> entity) {
            if (!mDone.getAndSet(true)) {
                mExecutor.execute(() -> super.onSuccess(call, entity));
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

    private static final class InnerParallelWorkerThreadExecutor extends AbstractExecutorService
    {
        public static class SingletonHolder {
            public static final InnerParallelWorkerThreadExecutor SHARED_INSTANCE = new InnerParallelWorkerThreadExecutor();
        }

        public static InnerParallelWorkerThreadExecutor instance() {
            return SingletonHolder.SHARED_INSTANCE;
        }

        private InnerParallelWorkerThreadExecutor() {
            // Do nothing
        }

        @Override
        public void execute(@NonNull Runnable runnable) {
            sThreadPoolExecutor.execute(runnable);
        }

        @Deprecated
        @Override
        public void shutdown() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public @NonNull List<Runnable> shutdownNow() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Deprecated
        @Override
        public boolean awaitTermination(long l, @NonNull TimeUnit timeUnit) throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
        private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
        private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
        private static final int KEEP_ALIVE = 1;

        private static final ThreadFactory sThreadFactory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread(final @NonNull Runnable runnable) {
                String threadName = InnerParallelWorkerThreadExecutor.class.getSimpleName() + " #" + mCount.getAndIncrement();

                return new Thread(() -> {
                    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                    runnable.run();
                }, threadName);
            }
        };

        private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<>(128);

        // An {@link Executor} that can be used to execute tasks in parallel.
        private final Executor sThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
    }

// MARK: - Constants

    private static final String TAG = TaskQueue.class.getSimpleName();

// MARK: - Variables

    private static final LinkedMultiValueMap<String, Cancellable> sTasks = new LinkedMultiValueMap<>();
    private static final Object sInnerLock = new Object();

}
