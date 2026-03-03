package com.vj.sampleaiassistant.domain.usecase

import com.vj.sampleaiassistant.data.local.database.MessageEntity
import com.vj.sampleaiassistant.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
class ObserveMessagesUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(): Flow<List<MessageEntity>> = repository.observeMessages()
}
