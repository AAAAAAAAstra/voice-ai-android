package com.example.voiceai.audio

import android.content.Context
import android.media.MediaRecorder
import java.io.File

class AudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun startRecording(onStart: (() -> Unit)? = null) {
        outputFile = File.createTempFile("recording", ".wav", context.cacheDir)
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile?.absolutePath)
            prepare()
            start()
        }
        onStart?.invoke()
    }

    fun stopRecordingAndTranscribe(modelPath: String): String? {
        recorder?.apply {
            stop()
            release()
        }
        val file = outputFile
        recorder = null
        outputFile = null
        return file?.let {
            WhisperJni.transcribe(it.absolutePath, modelPath)
        }
    }
}
