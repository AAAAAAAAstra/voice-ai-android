package com.example.voiceai.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OutputSection(
    responseText: String,
    audioUrl: String?,
    isPlaying: Boolean,
    onPlayAudio: () -> Unit,
    onPauseAudio: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "AI回复：", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = responseText, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        if (audioUrl != null) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Button(onClick = { if (isPlaying) onPauseAudio() else onPlayAudio() }) {
                    Text(if (isPlaying) "暂停播放" else "播放语音")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = audioUrl, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
} 