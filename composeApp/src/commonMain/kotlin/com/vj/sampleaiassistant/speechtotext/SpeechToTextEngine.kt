package com.vj.sampleaiassistant.speechtotext

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
expect class SpeechToTextEngine {
    suspend fun requestPermission(): Boolean
    fun startListening(onStateChange: (SpeechState) -> Unit)
    fun stopListening()
    fun release()
}