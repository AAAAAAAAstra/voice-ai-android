package com.example.voiceai.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voiceai.api.ApiClient
import com.example.voiceai.api.TextRequest
import com.example.voiceai.api.VoiceAIResponse
import com.example.voiceai.audio.AudioPlayer
import com.example.voiceai.audio.AudioRecorder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.UUID
import android.content.Context

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    private val deviceId = UUID.randomUUID().toString()
    private lateinit var audioRecorder: AudioRecorder
    private lateinit var audioPlayer: AudioPlayer
    
    fun initAudio(context: Context) {
        audioRecorder = AudioRecorder(context)
        audioPlayer = AudioPlayer(context)
    }
    
    fun startRecording() {
        _uiState.update { it.copy(isRecording = true) }
        audioRecorder.startRecording { /* 可添加实时上传逻辑 */ }
    }
    
    fun stopRecording() {
        _uiState.update { it.copy(isRecording = false, isLoading = true) }
        
        viewModelScope.launch {
            try {
                val audioFile = audioRecorder.stopRecording() ?: return@launch
                uploadAudio(audioFile)
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "录音处理失败: ${e.message}"
                )}
            }
        }
    }
    
    fun sendText(text: String) {
        if (text.isBlank()) return
        
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            try {
                val response = ApiClient.service.processText(
                    TextRequest(text),
                    deviceId
                )
                processResponse(response)
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "请求失败: ${e.message}"
                )}
            }
        }
    }
    
    private suspend fun uploadAudio(file: File) {
        val requestBody = file.asRequestBody("audio/mp4".toMediaTypeOrNull())
        val audioPart = MultipartBody.Part.createFormData("audio", file.name, requestBody)
        
        try {
            val response = ApiClient.service.processVoice(audioPart, deviceId)
            processResponse(response)
        } catch (e: Exception) {
            _uiState.update { it.copy(
                isLoading = false,
                error = "上传失败: ${e.message}"
            )}
        } finally {
            file.delete()
        }
    }
    
    private fun processResponse(response: VoiceAIResponse) {
        _uiState.update {
            it.copy(
                isLoading = false,
                responseText = response.ai_text,
                audioUrl = response.audio_url,
                error = null
            )
        }
    }
    
    fun playAudio() {
        uiState.value.audioUrl?.let { url ->
            audioPlayer.playAudio(url)
            _uiState.update { it.copy(isAudioPlaying = true) }
        }
    }
    
    fun pauseAudio() {
        audioPlayer.pause()
        _uiState.update { it.copy(isAudioPlaying = false) }
    }
    
    override fun onCleared() {
        audioPlayer.release()
        super.onCleared()
    }
}

data class MainUiState(
    val isRecording: Boolean = false,
    val isLoading: Boolean = false,
    val isAudioPlaying: Boolean = false,
    val responseText: String = "",
    val audioUrl: String? = null,
    val error: String? = null
)