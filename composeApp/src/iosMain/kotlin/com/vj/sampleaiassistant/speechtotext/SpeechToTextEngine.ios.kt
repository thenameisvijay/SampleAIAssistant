package com.vj.sampleaiassistant.speechtotext

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFAudio.AVAudioEngine
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryOptionDuckOthers
import platform.AVFAudio.AVAudioSessionCategoryRecord
import platform.AVFAudio.AVAudioSessionModeMeasurement
import platform.AVFAudio.setActive
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Speech.SFSpeechAudioBufferRecognitionRequest
import platform.Speech.SFSpeechRecognitionResult
import platform.Speech.SFSpeechRecognitionTask
import platform.Speech.SFSpeechRecognitionTaskDelegateProtocol
import platform.Speech.SFSpeechRecognizer
import platform.Speech.SFSpeechRecognizerAuthorizationStatus
import platform.Speech.SFTranscription
import platform.darwin.NSObject
import kotlin.coroutines.resume

actual class SpeechToTextEngine {

    private val speechRecognizer = SFSpeechRecognizer(locale = NSLocale.currentLocale)
    private val audioEngine = AVAudioEngine()
    private var request: SFSpeechAudioBufferRecognitionRequest? = null
    private var task: SFSpeechRecognitionTask? = null

    actual suspend fun requestPermission(): Boolean {
        val speechOk = suspendCancellableCoroutine<Boolean> { cont ->
            SFSpeechRecognizer.requestAuthorization { status ->
                cont.resume(status == SFSpeechRecognizerAuthorizationStatus.SFSpeechRecognizerAuthorizationStatusAuthorized)
            }
        }
        if (!speechOk) return false

        return suspendCancellableCoroutine { cont ->
            AVAudioSession.sharedInstance().requestRecordPermission { granted ->
                cont.resume(granted)
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun startListening(onStateChange: (SpeechState) -> Unit) {
        stopListening()

        val session = AVAudioSession.sharedInstance()
        session.setCategory(
            AVAudioSessionCategoryRecord,
            mode = AVAudioSessionModeMeasurement,
            options = AVAudioSessionCategoryOptionDuckOthers,
            error = null
        )
        session.setActive(true, error = null)

        request = SFSpeechAudioBufferRecognitionRequest().also {
            it.shouldReportPartialResults = true
        }

        val inputNode = audioEngine.inputNode
        val format = inputNode.outputFormatForBus(0u)
        inputNode.installTapOnBus(0u, bufferSize = 1024u, format = format) { buffer, _ ->
            request?.appendAudioPCMBuffer(buffer!!)
        }

        audioEngine.prepare()
        audioEngine.startAndReturnError(null)

        task = speechRecognizer?.recognitionTaskWithRequest(
            request = request!!,
            delegate = object : NSObject(), SFSpeechRecognitionTaskDelegateProtocol {

                override fun speechRecognitionTask(
                    task: SFSpeechRecognitionTask,
                    didHypothesizeTranscription: SFTranscription
                ) {
                    onStateChange(SpeechState.Result(didHypothesizeTranscription.formattedString))
                }

                override fun speechRecognitionTask(
                    task: SFSpeechRecognitionTask,
                    didFinishRecognition: SFSpeechRecognitionResult
                ) {
                    if (didFinishRecognition.isFinal()) {
                        onStateChange(
                            SpeechState.Result(didFinishRecognition.bestTranscription.formattedString)
                        )
                    }
                }

                override fun speechRecognitionTask(
                    task: SFSpeechRecognitionTask,
                    didFinishSuccessfully: Boolean
                ) {
                    if (!didFinishSuccessfully) {
                        task.error?.let {
                            onStateChange(SpeechState.Error(it.localizedDescription))
                        }
                    }
                }
            }
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun stopListening() {
        audioEngine.stop()
        audioEngine.inputNode.removeTapOnBus(0u)
        request?.endAudio()
        request = null
        task?.cancel()
        task = null
        try {
            AVAudioSession.sharedInstance().setActive(false, error = null)
        } catch (_: Exception) {
        }
    }

    actual fun release() = stopListening()
}