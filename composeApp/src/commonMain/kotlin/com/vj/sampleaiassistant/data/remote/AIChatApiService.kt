package com.vj.sampleaiassistant.data.remote

import com.vj.sampleaiassistant.data.model.MessageRequestModel
import com.vj.sampleaiassistant.data.model.MessageResponseModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */

const val BASE_URL = "" // Paste the IP address from Tailscale + port which you have given (eg:8000)
class AIChatApiService(private val client: HttpClient) {

    suspend fun sendMessage(request: MessageRequestModel): MessageResponseModel {
        return client.post("$BASE_URL/generate") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}