package com.vj.sampleaiassistant.presentation.chat

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Created by Vijay on 06/03/2026.
 * https://github.com/thenameisvijay
 */
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