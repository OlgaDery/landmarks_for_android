package com.google.albertasights.di

import android.content.Context
import androidx.annotation.NonNull
import com.google.albertasights.services.RetrofitFactory
import com.google.albertasights.services.RetrofitProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(@NonNull val context: Context) {

    @Provides
    @Singleton
    fun accessContext(): Context {
        return context
    }

    @Provides
    @Singleton
    fun accessRetrofitFactory(): RetrofitProvider {
        return RetrofitFactory()
    }

}