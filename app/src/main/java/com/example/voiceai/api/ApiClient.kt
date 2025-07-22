package com.example.voiceai.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface VoiceAIService {
    @Multipart
    @POST("process_voice")
    suspend fun processVoice(
        @Part audio: MultipartBody.Part,
        @Header("X-Session-ID") deviceId: String
    ): VoiceAIResponse

    @POST("process_text")
    suspend fun processText(
        @Body request: TextRequest,
        @Header("X-Session-ID") deviceId: String
    ): VoiceAIResponse

    @GET("get_audio_url")
    suspend fun getAudioUrl(
        @Query("text_hash") textHash: String
    ): AudioUrlResponse
}

data class TextRequest(val text: String)
data class VoiceAIResponse(
    val user_text: String?,
    val ai_text: String,
    val audio_url: String?,
    val timings: Map<String, Double>?,
    val text_hash: String?
)

data class AudioUrlResponse(
    val status: String,
    val audio_url: String?
)

object ApiClient {
    @Volatile
    private var baseUrl: String = "https://your-server.com/api/"

    fun setBaseUrl(url: String) {
        baseUrl = if (url.endsWith("/")) url else "$url/"
    }

    private fun getRetrofit(deviceId: String): Retrofit {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("X-Session-ID", deviceId)
                    .build()
                chain.proceed(request)
            }
            .build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getService(deviceId: String): VoiceAIService {
        return getRetrofit(deviceId).create(VoiceAIService::class.java)
    }
}