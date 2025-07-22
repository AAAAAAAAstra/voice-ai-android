package com.example.voiceai.api

import android.util.Log
import com.gonatural.opus.Encoder
import java.io.ByteArrayOutputStream

class OpusEncoder {
    private val encoder: Encoder
    private val outputStream = ByteArrayOutputStream()
    
    init {
        System.loadLibrary("opus")
        encoder = Encoder()
        encoder.init(16000, 1, Encoder.OPUS_APPLICATION_VOIP)
    }
    
    fun encode(pcmData: ByteArray): ByteArray {
        val encoded = encoder.encode(pcmData, pcmData.size)
        outputStream.write(encoded)
        return encoded
    }
    
    fun finalizeRecording(): ByteArray? {
        encoder.destroy()
        return try {
            outputStream.toByteArray().also {
                outputStream.close()
            }
        } catch (e: Exception) {
            Log.e("OpusEncoder", "Finalize failed", e)
            null
        }
    }
}