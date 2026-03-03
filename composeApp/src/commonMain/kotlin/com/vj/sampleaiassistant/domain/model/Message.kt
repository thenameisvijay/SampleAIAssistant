package com.vj.sampleaiassistant.domain.model

/**
 * Created by Vijay on 03/03/2026.
 * https://github.com/thenameisvijay
 */
data class Message(
    val id: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long,
    val isSentByMe: Boolean
)
