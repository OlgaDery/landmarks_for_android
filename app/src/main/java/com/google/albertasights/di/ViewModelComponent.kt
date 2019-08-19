package com.google.albertasights.di

import com.google.albertasights.MapViewModel
import dagger.Subcomponent

@Subcomponent(modules = [(ViewModelModule::class)])
interface ViewModelComponent {

    fun inject(viewModel: MapViewModel)
}