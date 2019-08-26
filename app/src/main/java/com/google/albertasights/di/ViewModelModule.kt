package com.google.albertasights.di

import android.content.Context
import com.google.albertasights.services.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ViewModelModule {

    @Provides
    @Singleton
    fun accessPreferences(context: Context): PreferencesProvider {
        return PreferenceActions(context)
    }

    @Provides
    @Singleton
    fun accessDataBase(context: Context): DatabaseProvider {
        return DatabaseActions(context)
    }

    @Provides
    @Singleton
    fun accessRetrofit(): GetGeoDataApi {
        return GetGeoData()
    }
}