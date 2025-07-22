package com.example.voiceai.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun InputSection(
    onTextSubmit: (String) -> Unit,
    onVoiceRecordStart: () -> Unit,
    onVoiceRecordStop: () -> Unit,
    isRecording: Boolean
) {
    var text by remember { mutableStateOf("") }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            label = { Text("请输入内容") }
        )
        Button(onClick = {
            onTextSubmit(text)
            text = ""
        }) {
            Text("发送")
        }
        Button(
            onClick = { if (!isRecording) onVoiceRecordStart() else onVoiceRecordStop() },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(if (isRecording) "停止" else "录音")
        }
        // Push to Talk 按住说话按钮
        Button(
            onClick = {}, // 禁用普通点击
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onVoiceRecordStart()
                        tryAwaitRelease()
                        onVoiceRecordStop()
                    }
                )
            }
        ) {
            Text("按住说话")
        }
    }
} 