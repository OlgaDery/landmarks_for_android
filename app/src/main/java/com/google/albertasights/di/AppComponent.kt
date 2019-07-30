package com.google.albertasights.di

import com.google.albertasights.MapViewModel
import com.google.albertasights.ui.MapsActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(activity: MapsActivity)

    fun inject(viewModel: MapViewModel)
}