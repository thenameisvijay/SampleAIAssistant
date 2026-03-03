package com.vj.sampleaiassistant.presentation.chat

import com.vj.sampleaiassistant.data.local.database.MessageEntity

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
data class ChatUiState(
    val messages: List<MessageEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSending: Boolean = false
)