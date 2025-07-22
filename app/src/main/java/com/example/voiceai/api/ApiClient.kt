package com.example.voiceai.api

import com.example.voiceai.BuildConfig
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface VoiceAIService {
    @Multipart
    @POST("/process_voice")
    suspend fun processVoice(
        @Part audio: MultipartBody.Part,
        @Header("X-Device-ID") deviceId: String
    ): VoiceAIResponse

    @POST("/process_text")
    suspend fun processText(
        @Body request: TextRequest,
        @Header("X-Device-ID") deviceId: String
    ): VoiceAIResponse
}

data class TextRequest(val text: String)
data class VoiceAIResponse(
    val user_text: String?,
    val ai_text: String,
    val audio_url: String?,
    val timings: Map<String, Double>?
)

object ApiClient {
    private const val BASE_URL = "https://your-server.com/api/"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
        }
        .build()
    
    val service: VoiceAIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VoiceAIService::class.java)
    }
}