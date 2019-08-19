package com.google.albertasights.di

import android.content.Context
import com.google.albertasights.services.Preferences
import com.google.albertasights.services.PreferencesProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ViewModelModule {

    @Provides
    @Singleton
    fun providePreferences(context: Context): PreferencesProvider {
        return Preferences(context)
    }
}