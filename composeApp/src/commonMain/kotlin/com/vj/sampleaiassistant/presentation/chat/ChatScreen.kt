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
    engine: SpeechToTextEngine
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
        onDispose { engine.release() }
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
                            if (!hasPermission) {
                                hasPermission = engine.requestPermission()
                            }
                            if (hasPermission) {
                                if (speechState is SpeechState.Listening) {
                                    engine.stopListening()
                                    speechState = SpeechState.Idle
                                } else {
                                    speechState = SpeechState.Listening
                                    engine.startListening { newState ->
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
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.messages) { message ->
                    println("Message: ${message.content}")
                    ChatBubble(message)
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun ChatBubble(message: MessageEntity) {
    val alignment = if (message.isSentByMe) Alignment.CenterEnd else Alignment.CenterStart
    val containerColor = if (message.isSentByMe)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.secondaryContainer

    val contentColor = if (message.isSentByMe)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSecondaryContainer

    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), contentAlignment = alignment) {
        Surface(
            color = containerColor,
            contentColor = contentColor,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isSentByMe) 16.dp else 4.dp,
                bottomEnd = if (message.isSentByMe) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun MicButton(
    isListening: Boolean,
    onToggleListening: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(contentAlignment = Alignment.Center) {
        if (isListening) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(pulseScale)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                        shape = CircleShape
                    )
            )
        }

        FloatingActionButton(
            onClick = onToggleListening,
            shape = CircleShape,
            containerColor = if (isListening) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier.size(72.dp)
        ) {
            Icon(
                imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = if (isListening) "Stop" else "Mic",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}