package com.vj.sampleaiassistant.data.repository

import com.vj.sampleaiassistant.data.datasource.LocalMessageDataSource
import com.vj.sampleaiassistant.data.datasource.RemoteMessageDataSource
import com.vj.sampleaiassistant.data.local.database.MessageEntity
import com.vj.sampleaiassistant.data.model.MessageResponseModel
import com.vj.sampleaiassistant.domain.model.ExceptionHandler
import com.vj.sampleaiassistant.domain.model.ResultHandler
import com.vj.sampleaiassistant.domain.repository.ChatRepository
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.utils.io.ioDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
class ChatRepositoryImpl(
    private val remoteDataSource: RemoteMessageDataSource,
    private val localDataSource: LocalMessageDataSource
) : ChatRepository {

    override suspend fun observeMessages(): Flow<List<MessageEntity>> {
        return localDataSource.observeAllMessages()
    }


    @OptIn(ExperimentalUuidApi::class)
    override suspend fun sendMessage(
        message: String
    ): ResultHandler<MessageResponseModel> = withContext(ioDispatcher()) {
        try {
            val userEntity = MessageEntity(
                id = Uuid.random().toHexString() + "_human", // Generate a unique ID
                senderId = "human",
                senderName = "Vijay",
                content = message,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                isSentByMe = true
            )
            localDataSource.insertMessage(userEntity)

            // 2. Call Remote API
            val response = remoteDataSource.sendMessage(message)

            if (response.statusCode == 200 || response.status?.equals(
                    "success",
                    ignoreCase = true
                ) == true
            ) {
                println("Bot Response: ${response.responseContent}")
                // 3. Create Bot Message Entity from response and save it locally
                val botEntity = MessageEntity(
                    id = Uuid.random().toHexString() + "_bot",
                    senderId = "bot",
                    senderName = "AI Assistant",
                    content = response.responseContent ?: "",
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                    isSentByMe = false
                )
                localDataSource.insertMessage(botEntity)

                ResultHandler.Success(response)
            } else {
                ResultHandler.Error(Exception("Failed to send message: Server returned success=false"))
            }
        } catch (e: HttpRequestTimeoutException) {
            ResultHandler.Error(ExceptionHandler.NetworkException(e))
        }
    }
}