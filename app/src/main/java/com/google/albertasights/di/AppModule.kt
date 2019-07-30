package com.google.albertasights.di

import android.content.Context
import androidx.annotation.NonNull
import com.google.albertasights.services.Preferences
import com.google.albertasights.services.PreferencesProvider
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
class AppModule(@NonNull val context: Context) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return context
    }


    @Provides
    @Singleton
    fun providePreferences(context: Context): PreferencesProvider {
        return Preferences(context)
    }

}