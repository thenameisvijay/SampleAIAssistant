package com.vj.sampleaiassistant.domain.usecase

import com.vj.sampleaiassistant.data.model.MessageResponseModel
import com.vj.sampleaiassistant.domain.model.ResultHandler
import com.vj.sampleaiassistant.domain.repository.ChatRepository

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
class SendMessageUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(
        message: String
    ): ResultHandler<MessageResponseModel> {
        if (message.isBlank()) {
            return ResultHandler.Error(Exception("Message cannot be empty"), "")
        }
        return repository.sendMessage(message)
    }
}