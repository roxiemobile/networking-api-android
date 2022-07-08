@file:Suppress("DEPRECATION", "SameParameterValue", "unused")

package com.roxiemobile.networkingapi.network.rest

import android.os.Process
import com.roxiemobile.androidcommons.concurrent.MainThreadExecutor
import com.roxiemobile.androidcommons.concurrent.ParallelWorkerThreadExecutor
import com.roxiemobile.androidcommons.concurrent.ThreadUtils
import com.roxiemobile.androidcommons.logging.Logger
import com.roxiemobile.networkingapi.network.http.util.LinkedMultiValueMap
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.RestApiError
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

object TaskQueue {

// MARK: - Methods

    /**
     * TODO
     */
    fun <Ti, To> enqueue(
        task: Task<Ti, To>,
        callback: Callback<Ti, To>? = null,
        callbackOnUiThread: Boolean = ThreadUtils.runningOnUiThread(),
    ): Cancellable {

        // Create new cancellable task
        val futureTask = InnerFutureTask(InnerRunnableTask(task, callback, callbackOnUiThread))
        _syncLock.write {
            _tasks.add(task.tag, futureTask)
        }

        // Execute the FutureTask on the background thread
        ParallelWorkerThreadExecutor.shared().execute(futureTask)

        // Done
        return futureTask
    }

    /**
     * TODO
     */
    fun cancel(tag: String) {
        _syncLock.write { _tasks.remove(tag) }?.forEach(Cancellable::cancel)
    }

// MARK: - Inner Types

    private class InnerFutureTask<Ti, To>: FutureTask<Void>, Cancellable {

        constructor(runnableTask: InnerRunnableTask<Ti, To>): super(runnableTask, null) {
            _runnableTask = runnableTask
        }

        override fun done() {
            super.done()

            // Remove the completed task
            _syncLock.read {
                val tasks: MutableList<Cancellable>? = _tasks[_runnableTask.tag]
                tasks?.remove(this)
            }
        }

        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            _runnableTask.cancel()
            return super.cancel(mayInterruptIfRunning)
        }

        override fun cancel(): Boolean {
            return cancel(true)
        }

        private val _runnableTask: InnerRunnableTask<Ti, To>
    }

    private class InnerRunnableTask<Ti, To>: Runnable, Cancellable {

        constructor(task: Task<Ti, To>, callback: Callback<Ti, To>?, callbackOnUiThread: Boolean) {
            _task = task.clone()
            _callback = InnerCallback(callback, callbackOnUiThread)
        }

        val tag: String?
            get() = _task.tag

        override fun run() {
            _task.execute(_callback)
        }

        override fun cancel(): Boolean {
            return _callback.cancel(_task)
        }

        private val _task: Task<Ti, To>
        private val _callback: InnerCallback<Ti, To>
    }

    private class InnerCallback<Ti, To>: CallbackDecorator<Ti, To> {

        constructor(callback: Callback<Ti, To>?, callbackOnUiThread: Boolean): super(callback) {
            _executor = when (callbackOnUiThread) {
                true -> MainThreadExecutor.shared()
                else -> InnerParallelWorkerThreadExecutor.SHARED
            }
        }

        override fun onShouldExecute(call: Call<Ti>): Boolean {
            return !_done.get() && awaitDone(_executor.submit<Boolean> { super.onShouldExecute(call) }, false)
        }

        override fun onSuccess(call: Call<Ti>, responseEntity: ResponseEntity<To>) {
            if (!_done.getAndSet(true)) {
                _executor.execute { super.onSuccess(call, responseEntity) }
            }
        }

        override fun onFailure(call: Call<Ti>, restApiError: RestApiError) {
            if (!_done.getAndSet(true)) {
                _executor.execute { super.onFailure(call, restApiError) }
            }
        }

        override fun onCancel(call: Call<Ti>) {
            if (!_done.getAndSet(true)) {
                _executor.execute { super.onCancel(call) }
            }
        }

        fun cancel(call: Call<Ti>): Boolean {
            val result = !_done.getAndSet(true)

            // Cancel the supplied task
            if (result) {
                try {
                    (call as Cancellable).cancel()
                }
                catch (ex: ClassCastException) {
                    Logger.w(TAG, ex)
                }
                awaitDone(_executor.submit { super.onCancel(call) })
            }
            return result
        }

        private fun <T> awaitDone(future: Future<T>, defaultValue: T): T {
            return awaitDone(future) ?: defaultValue
        }

        private fun <T> awaitDone(future: Future<T>): T? {
            var result: T? = null
            try {
                // Waits for the computation to complete
                result = future.get()
            }
            catch (ex: Exception) {
                when (ex) {
                    is ExecutionException,
                    is InterruptedException -> Logger.w(TAG, ex)
                    else -> throw ex
                }
            }
            return result
        }

        private val _executor: ExecutorService
        private val _done = AtomicBoolean(false)
    }

    private class InnerParallelWorkerThreadExecutor: AbstractExecutorService {

        private constructor()

        override fun execute(command: Runnable) {
            _threadPoolExecutor.execute(command)
        }

        override fun shutdown() {
            throw UnsupportedOperationException()
        }

        override fun shutdownNow(): MutableList<Runnable> {
            throw UnsupportedOperationException()
        }

        override fun isShutdown(): Boolean {
            return false
        }

        override fun isTerminated(): Boolean {
            return false
        }

        @Throws(InterruptedException::class)
        override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
            throw UnsupportedOperationException()
        }

        companion object {

            @JvmField
            val SHARED = InnerParallelWorkerThreadExecutor()

            private val CPU_COUNT: Int = Runtime.getRuntime().availableProcessors()
            private val CORE_POOL_SIZE: Int = CPU_COUNT + 1
            private val MAXIMUM_POOL_SIZE: Int = CPU_COUNT * 2 + 1
            private const val KEEP_ALIVE: Long = 1

            private val POOL_WORK_QUEUE: BlockingQueue<Runnable> = LinkedBlockingQueue(128)

            private val THREAD_FACTORY = object: ThreadFactory {
                override fun newThread(runnable: Runnable): Thread {

                    val target = Runnable {
                        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
                        runnable.run()
                    }

                    val simpleName = InnerParallelWorkerThreadExecutor::class.java.simpleName
                    val threadName = simpleName + " #" + _count.getAndIncrement()

                    return Thread(target, threadName)
                }

                private val _count = AtomicInteger(1)
            }
        }

        /**
         * An [Executor] that can be used to execute tasks in parallel.
         */
        private val _threadPoolExecutor: Executor = ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE,
            TimeUnit.SECONDS,
            POOL_WORK_QUEUE,
            THREAD_FACTORY,
        )
    }

// MARK: - Constants

    private val TAG = TaskQueue::class.java.simpleName

// MARK: - Variables

    private val _tasks = LinkedMultiValueMap<String, Cancellable>()

    private val _syncLock = ReentrantReadWriteLock()
}
