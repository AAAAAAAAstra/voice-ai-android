package com.example.voiceai.audio

import android.content.Context
import android.media.MediaPlayer

class AudioPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun playAudio(url: String) {
        stop()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener { start() }
            setOnCompletionListener { release() }
            prepareAsync()
        }
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun release() {
        stop()
    }
}
