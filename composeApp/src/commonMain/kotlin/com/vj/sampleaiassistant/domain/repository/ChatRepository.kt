package com.vj.sampleaiassistant.domain.repository

import com.vj.sampleaiassistant.data.local.database.MessageEntity
import com.vj.sampleaiassistant.data.model.MessageResponseModel
import com.vj.sampleaiassistant.domain.model.ResultHandler
import kotlinx.coroutines.flow.Flow

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
interface ChatRepository {
    suspend fun sendMessage(message: String): ResultHandler<MessageResponseModel>
    suspend fun observeMessages(): Flow<List<MessageEntity>>
}