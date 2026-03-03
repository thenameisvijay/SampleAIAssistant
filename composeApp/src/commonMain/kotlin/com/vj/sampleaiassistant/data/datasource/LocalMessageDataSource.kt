package com.vj.sampleaiassistant.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.vj.AIChatDatabase
import com.vj.sampleaiassistant.data.local.database.DatabaseDriverFactory
import com.vj.sampleaiassistant.data.local.database.MessageEntity
import com.vj.sampleaiassistant.data.model.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
class LocalMessageDataSource(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AIChatDatabase.Companion(databaseDriverFactory.createDriver())
    private val queries = database.aIChatDatabaseQueries

    /**
     * Observes all messages from the database as a Flow.
     */
    fun observeAllMessages(): Flow<List<MessageEntity>> = queries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }


    suspend fun insertMessage(
        entity: MessageEntity
    ) = withContext(Dispatchers.IO) {
        queries.insertMessage(
            id = entity.id,
            senderId = entity.senderId,
            senderName = entity.senderName,
            content = entity.content,
            timestamp = entity.timestamp,
            isSentByMe = entity.isSentByMe
        )
    }

    suspend fun deleteAllMessages() = withContext(Dispatchers.IO) {
        queries.deleteAllMessages()
    }
}