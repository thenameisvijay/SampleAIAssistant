package com.vj.sampleaiassistant.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
@Serializable
data class MessageRequestModel(
    @SerialName("senderId")
    val senderId: String?,
    @SerialName("senderName")
    val senderName: String?,
    @SerialName("prompt")
    val prompt: String
)