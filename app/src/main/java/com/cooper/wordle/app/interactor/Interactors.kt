package com.cooper.wordle.app.interactor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

/**
 * Interactor that returns results using [Flow].
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class FlowInteractor<P : Any, T> {

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

abstract class SuspendingWorkInteractor<P : Any, T> : FlowInteractor<P, T>() {

    override fun createFlow(params: P): Flow<T> = flow {
        emit(doWork(params))
    }

    abstract suspend fun doWork(params: P): T
}