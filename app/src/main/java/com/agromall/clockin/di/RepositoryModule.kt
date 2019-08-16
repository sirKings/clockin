package com.agromall.clockin.di

import com.agromall.clockin.data.repo.Repository
import com.agromall.clockin.data.source.DataSource
import com.agromall.clockin.data.source.remote.RemoteDataSource
import org.koin.dsl.module

val repoModule = module {
    single { RemoteDataSource(get()) as DataSource }
    single { Repository(get(), get()) }
}