package com.vj.sampleaiassistant.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
@Serializable
data class MessageResponseModel(
    @SerialName("statusCode")
    val statusCode: Int? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("responseContent")
    val responseContent: String? = null
)
