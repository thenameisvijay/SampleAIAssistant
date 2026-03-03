package com.vj.sampleaiassistant.data.local.database

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
@Serializable
data class MessageEntity(
    @SerialName("id")
    val id: String,
    @SerialName("senderId")
    val senderId: String,
    @SerialName("senderName")
    val senderName: String,
    @SerialName("content")
    val content: String,
    @SerialName("timestamp")
    val timestamp: Long,
    @SerialName("isSentByMe")
    val isSentByMe: Boolean,
)
