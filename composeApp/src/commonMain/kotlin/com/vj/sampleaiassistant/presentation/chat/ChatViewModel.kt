package com.vj.sampleaiassistant.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vj.sampleaiassistant.presentation.chat.ChatUiState
import com.vj.sampleaiassistant.domain.model.ResultHandler
import com.vj.sampleaiassistant.domain.usecase.ObserveMessagesUseCase
import com.vj.sampleaiassistant.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */

class ChatViewModel(
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        observeMessages()
    }

    private fun observeMessages() {
        viewModelScope.launch {
            observeMessagesUseCase()
                .collect { allMessages ->
                    _uiState.update { it.copy(messages = allMessages) }
                }
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true, error = null) }

            when (val result = sendMessageUseCase(content)) {
                is ResultHandler.Success -> {
                    _uiState.update { it.copy(isSending = false, error = null) }
                }
                is ResultHandler.Error -> {
                    _uiState.update {
                        it.copy(isSending = false, error = result.message ?: "Failed to send message")
                    }
                }
                is ResultHandler.Loading -> {
                    _uiState.update { it.copy(isSending = true) }
                }
            }
        }
    }
}
