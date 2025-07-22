package com.example.voiceai.audio

object WhisperJni {
    init {
        System.loadLibrary("whisper")
    }
    external fun transcribe(audioPath: String, modelPath: String): String
} 