package com.google.albertasights.services

import com.google.albertasights.ConfigValues
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RetrofitFactory @Inject constructor(): RetrofitProvider {

    override fun retrofitFactory(url: String): Retrofit {
        val gson = GsonBuilder()
        gson.setLenient()

        val okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()

        val retrofitBuilder = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson.create()))
        val clientBuilder = OkHttpClient.Builder()
        retrofitBuilder.client(clientBuilder.build())
        return retrofitBuilder.build()
    }
}