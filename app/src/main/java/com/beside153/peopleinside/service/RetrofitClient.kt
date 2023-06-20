package com.beside153.peopleinside.service

import com.beside153.peopleinside.view.App.Companion.sharedPreferences
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import java.io.IOException

object RetrofitClient {
    private const val baseUrl = "http://27.96.131.197/api/"
    private const val contentType = "application/json"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val signUpRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(json.asConverterFactory(contentType.toMediaType()))
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(provideOkHttpClient(AppInterceptor()))
        .addConverterFactory(json.asConverterFactory(contentType.toMediaType()))
        .build()

    private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient = OkHttpClient.Builder().run {
        addInterceptor(interceptor)
        build()
    }

    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val jwtToken = sharedPreferences.getString("JWT_TOKEN", "").toString()
            val newRequest = request().newBuilder()
                .addHeader("authorization", "Bearer $jwtToken")
                .build()
            proceed(newRequest)
        }
    }

    val signUpService: SignUpService = signUpRetrofit.create(SignUpService::class.java)
    val mbtiService: MbtiService = retrofit.create(MbtiService::class.java)
}
