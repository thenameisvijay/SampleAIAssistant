package com.vj.sampleaiassistant.speechtotext

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFAudio.AVAudioEngine
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryRecord
import platform.AVFAudio.inputAvailable
import platform.AVFAudio.setActive
import platform.Foundation.NSError
import platform.Foundation.NSLocale
import platform.Foundation.NSProcessInfo
import platform.Foundation.NSString
import platform.Foundation.NSTimer
import platform.Foundation.currentLocale
import platform.Speech.SFSpeechAudioBufferRecognitionRequest
import platform.Speech.SFSpeechRecognitionResult
import platform.Speech.SFSpeechRecognitionTask
import platform.Speech.SFSpeechRecognitionTaskDelegateProtocol
import platform.Speech.SFSpeechRecognitionTaskHintDictation
import platform.Speech.SFSpeechRecognizer
import platform.Speech.SFSpeechRecognizerAuthorizationStatus
import platform.Speech.SFTranscription
import platform.darwin.NSObject
import kotlin.coroutines.resume

actual class SpeechToTextEngine {

    private var speechRecognizer: SFSpeechRecognizer? = null
    private val audioEngine = AVAudioEngine()
    private var recognitionRequest: SFSpeechAudioBufferRecognitionRequest? = null
    private var recognitionTask: SFSpeechRecognitionTask? = null
    private var recognitionDelegate: NSObject? = null
    private var mockTimer: NSTimer? = null

    private var isListening = false
    private var isCleaningUp = false

    private val isSimulator: Boolean by lazy {
        val env = NSProcessInfo.processInfo.environment
        val primary = env["SIMULATOR_DEVICE_NAME"] as? NSString
        val fallback = env["SIMULATOR_MODEL_IDENTIFIER"] as? NSString
        primary != null || fallback != null
    }

    actual suspend fun requestPermission(): Boolean {
        if (isSimulator) return true

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
        if (isListening) stopListening()
        isCleaningUp = false

        if (isSimulator) {
            startSimulatorMock(onStateChange)
            return
        }

        val recognizer = SFSpeechRecognizer(locale = NSLocale.currentLocale) ?: run {
            onStateChange(SpeechState.Error("Speech recognizer not available"))
            return
        }
        if (!recognizer.available) {
            onStateChange(SpeechState.Error("Speech recognizer is not currently available"))
            return
        }
        speechRecognizer = recognizer
        val session = AVAudioSession.sharedInstance()

        if (!session.inputAvailable) {
            onStateChange(SpeechState.Error("Microphone input is not available"))
            return
        }

        val sessionConfigured = memScoped {
            val err = alloc<ObjCObjectVar<NSError?>>()
            session.setCategory(AVAudioSessionCategoryRecord, error = err.ptr)
            err.value?.let {
                onStateChange(SpeechState.Error("Audio session error: ${it.localizedDescription}"))
                return@memScoped false
            }
            session.setActive(true, error = err.ptr)
            err.value?.let {
                onStateChange(SpeechState.Error("Audio activation error: ${it.localizedDescription}"))
                return@memScoped false
            }
            true
        }
        if (!sessionConfigured) return

        val request = SFSpeechAudioBufferRecognitionRequest().also { req ->
            req.shouldReportPartialResults = true
            req.taskHint = SFSpeechRecognitionTaskHintDictation
        }
        recognitionRequest = request

        val inputNode = audioEngine.inputNode

        try {
            inputNode.removeTapOnBus(0u)
        } catch (_: Exception) {
        }

        audioEngine.prepare()
        val recordingFormat = inputNode.outputFormatForBus(0u)

        try {
            inputNode.installTapOnBus(
                bus = 0u,
                bufferSize = 4096u,
                format = recordingFormat
            ) { buffer, _ ->
                if (!isCleaningUp && buffer != null) {
                    request.appendAudioPCMBuffer(buffer)
                }
            }
        } catch (e: Exception) {
            onStateChange(SpeechState.Error("Failed to install audio tap: ${e.message}"))
            return
        }

        val delegate = object : NSObject(), SFSpeechRecognitionTaskDelegateProtocol {

            override fun speechRecognitionTask(
                task: SFSpeechRecognitionTask,
                didHypothesizeTranscription: SFTranscription
            ) {
                if (!isCleaningUp)
                    onStateChange(SpeechState.PartialResult(didHypothesizeTranscription.formattedString))
            }

            override fun speechRecognitionTask(
                task: SFSpeechRecognitionTask,
                didFinishRecognition: SFSpeechRecognitionResult
            ) {
                if (!isCleaningUp && didFinishRecognition.isFinal())
                    onStateChange(SpeechState.Result(didFinishRecognition.bestTranscription.formattedString))
            }

            override fun speechRecognitionTask(
                task: SFSpeechRecognitionTask,
                didFinishSuccessfully: Boolean
            ) {
                if (isCleaningUp) return

                if (!didFinishSuccessfully) {
                    task.error?.let { err ->
                        when (err.code) {
                            1103L -> onStateChange(SpeechState.Idle)
                            1100L -> onStateChange(SpeechState.Error("Recognition canceled"))
                            1101L -> onStateChange(SpeechState.Error("Audio input error"))
                            1104L -> onStateChange(SpeechState.Error("Request timed out"))
                            1105L -> onStateChange(SpeechState.Error("Speech recognition service unavailable"))
                            else -> onStateChange(
                                SpeechState.Error(
                                    err.localizedDescription ?: "Unknown error (code ${err.code})"
                                )
                            )
                        }
                    }
                }
            }
        }
        recognitionDelegate = delegate

        recognitionTask = recognizer.recognitionTaskWithRequest(
            request = request,
            delegate = delegate
        )

        val engineStarted = memScoped {
            val err = alloc<ObjCObjectVar<NSError?>>()
            val started = audioEngine.startAndReturnError(err.ptr)
            if (!started || err.value != null) {
                val msg = err.value?.localizedDescription ?: "Unknown audio engine error"
                onStateChange(SpeechState.Error("Could not start audio engine: $msg"))
                false
            } else true
        }

        if (engineStarted) {
            isListening = true
            onStateChange(SpeechState.Listening)
        } else {
            stopListening()
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun stopListening() {
        isCleaningUp = true

        if (isSimulator) {
            mockTimer?.invalidate()
            mockTimer = null
            isListening = false
            isCleaningUp = false
            return
        }

        try {
            recognitionTask?.cancel(); recognitionTask = null
        } catch (_: Exception) {
        }
        try {
            recognitionRequest?.endAudio(); recognitionRequest = null
        } catch (_: Exception) {
        }
        try {
            if (audioEngine.running) audioEngine.stop()
        } catch (_: Exception) {
        }
        try {
            audioEngine.inputNode.removeTapOnBus(0u)
        } catch (_: Exception) {
        }
        try {
            audioEngine.reset()
        } catch (_: Exception) {
        }
        try {
            AVAudioSession.sharedInstance().setActive(false, error = null)
        } catch (_: Exception) {
        }

        recognitionDelegate = null
        isListening = false
        isCleaningUp = false
    }

    actual fun release() {
        stopListening()
        speechRecognizer = null
    }

    private fun startSimulatorMock(onStateChange: (SpeechState) -> Unit) {
        val mockPhrases = listOf(
            "Can you teach me AI?"
        )
        var step = 0
        isListening = true
        onStateChange(SpeechState.Listening)

        mockTimer = NSTimer.scheduledTimerWithTimeInterval(
            interval = 0.6,
            repeats = true,
            block = { _ ->
                if (isCleaningUp || step >= mockPhrases.size) {
                    mockTimer?.invalidate()
                    mockTimer = null
                    if (!isCleaningUp) {
                        onStateChange(SpeechState.Result(mockPhrases.last()))
                        isListening = false
                    }
                    return@scheduledTimerWithTimeInterval
                }
                onStateChange(SpeechState.PartialResult(mockPhrases[step]))
                step++
            }
        )
    }
}
