package com.mindorks.example.coroutines.utils

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicReference

class ControlledRunner<T> {
    private val activeTask = AtomicReference<Deferred<T>?>(null)

    suspend fun cancelPreviousThenRun(block: suspend() -> T): T {
        activeTask.get()?.cancelAndJoin()

        return coroutineScope {
            val newTask = async(start = CoroutineStart.LAZY) {
                block()
            }

            newTask.invokeOnCompletion {
                activeTask.compareAndSet(newTask, null)
            }
            val result: T
            while(true) {
                if (!activeTask.compareAndSet(null, newTask)) {
                    activeTask.get()?.cancelAndJoin()
                    yield()
                } else {
                    result = newTask.await()
                    break
                }
            }
            result
        }
    }

    suspend fun joinPreviousOrRun(block: suspend () -> T): T {
        activeTask.get()?.let {
            return it.await()
        }
        return coroutineScope {
            val newTask = async(start = CoroutineStart.LAZY) {
                block()
            }

            newTask.invokeOnCompletion {
                activeTask.compareAndSet(newTask, null)
            }

            val result: T
            while(true) {
                if (!activeTask.compareAndSet(null, newTask)) {
                    val currentTask = activeTask.get()
                    if (currentTask != null) {
                        newTask.cancel()
                        result = currentTask.await()
                        break
                    } else {
                        yield()
                    }
                } else {
                    result = newTask.await()
                    break
                }
            }

            result
        }
    }
}