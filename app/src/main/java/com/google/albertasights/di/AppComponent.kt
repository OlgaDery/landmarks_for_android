package com.google.albertasights.di

import com.google.albertasights.MapViewModel
import com.google.albertasights.MapsActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    //val context Context

    fun inject(activity: MapsActivity)
    fun addComponent(viewModelModule: ViewModelModule): ViewModelComponent
}