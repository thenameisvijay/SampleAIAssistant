package com.vj.sampleaiassistant.domain.repository

import com.vj.sampleaiassistant.speechtotext.SpeechState

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
interface SpeechToTextRepository {
    suspend fun requestPermission(): Boolean
    fun startListening(onStateChange: (SpeechState) -> Unit)
    fun stopListening()
    fun release()
}