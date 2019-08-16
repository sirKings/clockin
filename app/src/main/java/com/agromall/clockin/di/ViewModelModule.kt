package com.agromall.clockin.di

import com.agromall.clockin.ui.main.MainViewModel
import com.agromall.clockin.util.AppSchedulers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
    single { AppSchedulers() }
}