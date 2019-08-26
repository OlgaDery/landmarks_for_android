package com.google.albertasights.di

import com.google.albertasights.MapsActivity
import com.google.albertasights.services.GetGeoData
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(activity: MapsActivity)
    fun inject(mapDataService: GetGeoData)
    fun addComponent(viewModelModule: ViewModelModule): ViewModelComponent
}