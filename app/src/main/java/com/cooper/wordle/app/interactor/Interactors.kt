package com.cooper.wordle.app.interactor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit

sealed class InvokeStatus
object InvokeStarted : InvokeStatus()
object InvokeSuccess : InvokeStatus()
data class InvokeError(val throwable: Throwable) : InvokeStatus()

/**
 * Interactor designed to work with suspending functions that doesn't return anything.
 */
abstract class Interactor<in P> {

    operator fun invoke(params: P, timeoutMs: Long = defaultTimeoutMs): Flow<InvokeStatus> {
        return flow {
            withTimeout(timeoutMs) {
                emit(InvokeStarted)
                doWork(params)
                emit(InvokeSuccess)
            }
        }.catch { t ->
            emit(InvokeError(t))
        }
    }

    suspend fun executeSync(params: P) = doWork(params)

    protected abstract suspend fun doWork(params: P)

    companion object {
        private val defaultTimeoutMs = TimeUnit.MINUTES.toMillis(1)
    }
}

/**
 * Interactor that returns results using [Flow].
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class FlowInteractor<P : Any?, T> {

    private val paramState = MutableSharedFlow<P>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val flow: Flow<T> = paramState
        .distinctUntilChanged()
        .flatMapLatest { createFlow(it) }
        .distinctUntilChanged()

    operator fun invoke(params: P) {
        paramState.tryEmit(params)
    }

    protected abstract fun createFlow(params: P): Flow<T>
}