package com.cooper.wordle.app.ui.common

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class UiMessage(val message: String)

class SnackbarManager @Inject constructor() {

    private val messageQueue = Channel<UiMessage>(3, BufferOverflow.DROP_OLDEST)
    private val removeMessage = Channel<Unit>(Channel.RENDEZVOUS)

    val messages: Flow<UiMessage?> = flow {
        emit(null)

        messageQueue.receiveAsFlow().collect { message ->
            emit(message)

            merge(
                flow {
                    delay(5000)
                    emit(Unit)
                },
                removeMessage.receiveAsFlow()
            ).firstOrNull()

            emit(null)
        }
    }

    suspend fun addMessage(message: UiMessage) {
        messageQueue.send(message)
    }

    suspend fun removeMessage() {
        removeMessage.send(Unit)
    }
}