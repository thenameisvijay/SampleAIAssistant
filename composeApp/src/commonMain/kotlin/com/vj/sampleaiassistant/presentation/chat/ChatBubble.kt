package com.vj.sampleaiassistant.presentation.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vj.sampleaiassistant.data.local.database.MessageEntity

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */

@Composable
fun ChatBubble(message: MessageEntity) {
    val alignment = if (message.isSentByMe) Alignment.CenterEnd else Alignment.CenterStart
    val containerColor = if (message.isSentByMe)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.secondaryContainer

    val contentColor = if (message.isSentByMe)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSecondaryContainer

    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), contentAlignment = alignment) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            if (!message.isSentByMe) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "Bot",
                    modifier = Modifier.padding(bottom = 4.dp, end = 4.dp).size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Surface(
                color = containerColor,
                contentColor = contentColor,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (message.isSentByMe) 16.dp else 4.dp,
                    bottomEnd = if (message.isSentByMe) 4.dp else 16.dp
                ),
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 16.sp
                )
            }

            if (message.isSentByMe) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Me",
                    modifier = Modifier.padding(bottom = 4.dp, start = 4.dp).size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
