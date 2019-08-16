package com.agromall.clockin.di

import com.agromall.clockin.data.source.local.AppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val roomModule = module {
    single { AppDatabase.getInstance(androidApplication()) }
    single { get<AppDatabase>().getDao() }
}