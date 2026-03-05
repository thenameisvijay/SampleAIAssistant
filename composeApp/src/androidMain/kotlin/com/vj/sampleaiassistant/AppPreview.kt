package com.vj.sampleaiassistant

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.vj.sampleaiassistant.presentation.chat.ChatScreen
import com.vj.sampleaiassistant.presentation.theme.ChatAppTheme
import com.vj.sampleaiassistant.speechtotext.SpeechToTextEngine

@Preview
@Composable
fun AppPreview(engine: SpeechToTextEngine? = null) {
    ChatAppTheme {
        ChatScreen(engine = engine)
    }
}
