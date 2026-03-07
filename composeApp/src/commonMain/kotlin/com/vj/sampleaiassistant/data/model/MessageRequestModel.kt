package com.vj.sampleaiassistant.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
@Serializable
data class MessageRequestModel(
    @SerialName("prompt")
    val prompt: String
)