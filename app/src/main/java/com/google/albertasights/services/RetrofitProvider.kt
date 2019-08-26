package com.google.albertasights.services

import com.google.albertasights.ConfigValues
import retrofit2.Retrofit

interface RetrofitProvider {

    fun retrofitFactory(url: String = ConfigValues.BASE_URL): Retrofit
}