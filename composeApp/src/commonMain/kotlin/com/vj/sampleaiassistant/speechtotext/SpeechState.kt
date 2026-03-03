package com.vj.sampleaiassistant.speechtotext

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
sealed class SpeechState {
    object Idle : SpeechState()
    object Listening : SpeechState()
    data class PartialResult(val text: String) : SpeechState() // For in-progress speech
    data class Result(val text: String) : SpeechState() // For the final result
    data class Error(val message: String) : SpeechState()
}