package com.agromall.clockin

import android.app.Application
import com.agromall.clockin.di.networkModule
import com.agromall.clockin.di.repoModule
import com.agromall.clockin.di.roomModule
import com.agromall.clockin.di.viewModelModule
import com.agromall.clockin.util.TimberTree
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class BaseApplication: Application(){

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(viewModelModule, networkModule, repoModule, roomModule)
        }

        if (BuildConfig.DEBUG)
            Timber.plant(TimberTree())
    }

}