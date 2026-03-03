package com.vj.sampleaiassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.vj.sampleaiassistant.speechtotext.SpeechToTextEngine

class MainActivity : ComponentActivity() {

    private lateinit var engine: SpeechToTextEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        engine = SpeechToTextEngine(this)  // Activity needed for permission launcher
        setContent {
            App(engine)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        engine.release()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    lateinit var engine: SpeechToTextEngine
    App(engine)
}