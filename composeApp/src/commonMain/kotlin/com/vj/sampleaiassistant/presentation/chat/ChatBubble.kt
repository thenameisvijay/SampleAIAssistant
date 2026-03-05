package com.vj.sampleaiassistant.presentation.chat

import androidx.compose.runtime.Composable
import com.vj.sampleaiassistant.data.local.database.MessageEntity

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
//@Preview(showBackground = true)
@Composable
fun ChatBubbleSentPreview() {
    ChatBubble(
        message = MessageEntity(
            id = "1",
            senderId = "user1",
            senderName = "Vijay",
            content = "Hello, how can I help you today?",
            timestamp = 1771581600000L, // 2026-03-02 10:00:00 UTC
            isSentByMe = true
        )
    )
}

//@Preview(showBackground = true)
@Composable
fun ChatBubbleReceivedPreview() {
    ChatBubble(
        message = MessageEntity(
            id = "2",
            senderId = "ai",
            senderName = "AI Assistant",
            content = "I'm looking for a way to fix you.",
            timestamp = 1771581660000L, // 2026-03-02 10:01:00 UTC
            isSentByMe = false
        )
    )
}
