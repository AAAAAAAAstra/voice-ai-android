package com.example.voiceai.audio

import android.content.Context
import android.media.MediaRecorder
import java.io.File

class AudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun startRecording(onStart: (() -> Unit)? = null) {
        outputFile = File.createTempFile("recording", ".opus", context.cacheDir)
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            setAudioEncoder(MediaRecorder.AudioEncoder.OPUS)
            setOutputFile(outputFile?.absolutePath)
            prepare()
            start()
        }
        onStart?.invoke()
    }

    fun stopRecording(): File? {
        recorder?.apply {
            stop()
            release()
        }
        val file = outputFile
        recorder = null
        outputFile = null
        return file
    }
}
