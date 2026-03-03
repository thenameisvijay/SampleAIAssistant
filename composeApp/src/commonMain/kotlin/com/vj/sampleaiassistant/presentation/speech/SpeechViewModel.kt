package com.vj.sampleaiassistant.presentation.speech

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vj.sampleaiassistant.domain.repository.SpeechToTextRepository
import com.vj.sampleaiassistant.speechtotext.SpeechState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
class SpeechViewModel(private val repository: SpeechToTextRepository
) : ViewModel() {

    private val _state = MutableStateFlow<SpeechState>(SpeechState.Idle)
    val state: StateFlow<SpeechState> = _state.asStateFlow()

    fun onMicClick() {
        viewModelScope.launch {
            if (repository.requestPermission()) {
                _state.value = SpeechState.Listening
                repository.startListening { newState ->
                    _state.value = newState
                }
            }
        }
    }

    fun stopListening() {
        repository.stopListening()
        _state.value = SpeechState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        repository.release()
    }
}