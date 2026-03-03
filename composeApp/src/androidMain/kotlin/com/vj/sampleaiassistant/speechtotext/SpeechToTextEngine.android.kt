package com.vj.sampleaiassistant.speechtotext

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.vj.sampleaiassistant.speechtotext.SpeechState
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

actual class SpeechToTextEngine(private val activity: ComponentActivity) {

    private var recognizer: SpeechRecognizer? = null
    private var permissionCallback: ((Boolean) -> Unit)? = null

    private val permissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionCallback?.invoke(granted)
        permissionCallback = null
    }

    actual suspend fun requestPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                activity, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) return true

        return suspendCancellableCoroutine { cont ->
            permissionCallback = { cont.resume(it) }
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    actual fun startListening(onStateChange: (SpeechState) -> Unit) {
        recognizer?.destroy()
        recognizer = SpeechRecognizer.createSpeechRecognizer(activity).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    onStateChange(SpeechState.Listening)
                }
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onEvent(eventType: Int, params: Bundle?) {}

                override fun onPartialResults(partial: Bundle?) {
                    val text = partial
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull() ?: return
                    onStateChange(SpeechState.PartialResult(text))
                }

                override fun onResults(results: Bundle?) {
                    val text = results
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull() ?: ""
                    onStateChange(SpeechState.Result(text))
                }

                override fun onError(error: Int) {
                    val msg = when (error) {
                        SpeechRecognizer.ERROR_AUDIO              -> "Audio error"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permission denied"
                        SpeechRecognizer.ERROR_NETWORK            -> "Network error"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT    -> "Network timeout"
                        SpeechRecognizer.ERROR_NO_MATCH           -> "No speech matched"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY    -> "Recognizer busy"
                        SpeechRecognizer.ERROR_SERVER             -> "Server error"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT     -> "No speech detected"
                        else -> "Error code $error"
                    }
                    onStateChange(SpeechState.Error(msg))
                }
            })
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        recognizer?.startListening(intent)
    }

    actual fun stopListening() { recognizer?.stopListening() }

    actual fun release() {
        recognizer?.destroy()
        recognizer = null
    }
}