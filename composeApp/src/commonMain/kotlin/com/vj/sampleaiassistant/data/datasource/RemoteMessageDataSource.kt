package com.vj.sampleaiassistant.data.datasource

import com.vj.sampleaiassistant.data.model.MessageRequestModel
import com.vj.sampleaiassistant.data.model.MessageResponseModel
import com.vj.sampleaiassistant.data.remote.AIChatApiService

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
class RemoteMessageDataSource(private val api: AIChatApiService
) {
    suspend fun sendMessage(message: String): MessageResponseModel {
        return api.sendMessage(MessageRequestModel("321", "Vijay", prompt = message))
    }
}