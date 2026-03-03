package com.vj.sampleaiassistant

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.vj.sampleaiassistant.di.initKoin
import com.vj.sampleaiassistant.speechtotext.SpeechToTextEngine

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    val engine = remember { SpeechToTextEngine() }
    App(engine)
}