package com.example.voiceai.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voiceai.ui.components.*
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                // 可以在此处提示用户权限被拒绝
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestAudioPermission()
        setContent {
            VoiceAITheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }

    private fun requestAudioPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 已获得权限
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(modifier = Modifier.padding(16.dp)) {
        // 输入区域
        InputSection(
            onTextSubmit = { text -> viewModel.sendText(text) },
            onVoiceRecordStart = { viewModel.startRecording() },
            onVoiceRecordStop = { viewModel.stopRecording() },
            isRecording = uiState.isRecording
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 输出区域
        OutputSection(
            responseText = uiState.responseText,
            audioUrl = uiState.audioUrl,
            isPlaying = uiState.isAudioPlaying,
            onPlayAudio = { viewModel.playAudio() },
            onPauseAudio = { viewModel.pauseAudio() }
        )
        
        // 状态指示器
        when {
            uiState.isLoading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            uiState.error != null -> ErrorMessage(message = uiState.error ?: "未知错误")
        }
    }
}