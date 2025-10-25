package com.ml.mindloop.facenet_android

import android.app.Application
import com.ml.mindloop.facenet_android.data.ObjectBoxStore
import com.ml.mindloop.facenet_android.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize ObjectBox first
        ObjectBoxStore.init(this)

        // Then initialize Koin
        startKoin {
            androidContext(this@MainApplication)
            modules(AppModule().module)
        }
    }
}
