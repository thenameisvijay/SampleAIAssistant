package com.vj.sampleaiassistant.presentation.chat

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vj.sampleaiassistant.data.local.database.MessageEntity
import com.vj.sampleaiassistant.speechtotext.SpeechState
import com.vj.sampleaiassistant.speechtotext.SpeechToTextEngine
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    engine: SpeechToTextEngine? = null // Made nullable to support Previews
) {
    val viewModel = koinViewModel<ChatViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var speechState by remember { mutableStateOf<SpeechState>(SpeechState.Idle) }
    var hasPermission by remember { mutableStateOf(false) }

    // Auto-scroll to bottom
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    // Handle speech results
    LaunchedEffect(speechState) {
        when (val state = speechState) {
            is SpeechState.Result -> {
                if (state.text.isNotBlank()) {
                    println("Success: ${state.text}")
                    viewModel.sendMessage(state.text)
                }
                speechState = SpeechState.Idle
            }
            is SpeechState.Error -> {
                println("Speech Error: ${state.message}") // Check Logcat for this!
                speechState = SpeechState.Idle
            }
            else -> {}
        }
    }

    DisposableEffect(engine) {
        onDispose { engine?.release() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Assistant") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (speechState is SpeechState.Listening) {
                    Text("Listening...", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 8.dp))
                }
                MicButton(
                    isListening = speechState is SpeechState.Listening,
                    onToggleListening = {
                        scope.launch {
                            val currentEngine = engine ?: return@launch
                            if (!hasPermission) {
                                hasPermission = currentEngine.requestPermission()
                            }
                            if (hasPermission) {
                                if (speechState is SpeechState.Listening) {
                                    currentEngine.stopListening()
                                    speechState = SpeechState.Idle
                                } else {
                                    speechState = SpeechState.Listening
                                    currentEngine.startListening { newState ->
                                        speechState = newState
                                    }
                                }
                            }
                        }
                    }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.messages.isEmpty() && !uiState.isLoading) {
                WelcomeMessage()
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.messages) { message ->
                        ChatBubble(message)
                    }
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}