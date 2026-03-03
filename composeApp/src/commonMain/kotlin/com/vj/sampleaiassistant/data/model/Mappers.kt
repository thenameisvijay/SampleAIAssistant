package com.vj.sampleaiassistant.data.model

import com.vj.MessageTable
import com.vj.sampleaiassistant.data.local.database.MessageEntity
import com.vj.sampleaiassistant.domain.model.Message

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
fun MessageTable.toDomain(): MessageEntity = MessageEntity(
    id = id,
    senderId = senderId,
    senderName = senderName,
    content = content,
    timestamp = timestamp,
    isSentByMe = isSentByMe
)


fun MessageEntity.toDomainModel(): Message = Message(
    id = id,
    senderId = senderId,
    senderName = senderName,
    content = content,
    timestamp = timestamp,
    isSentByMe = isSentByMe
)

fun Message.toEntity(): MessageEntity = MessageEntity(
    id = id,
    senderId = senderId,
    senderName = senderName,
    content = content,
    timestamp = timestamp,
    isSentByMe = isSentByMe
)