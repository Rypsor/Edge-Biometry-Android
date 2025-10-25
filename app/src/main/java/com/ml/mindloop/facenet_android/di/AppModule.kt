package com.ml.mindloop.facenet_android.di

import com.ml.mindloop.facenet_android.data.ObjectBoxStore
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.ml.mindloop.facenet_android")
class AppModule {
    @Single
    fun provideBoxStore() = ObjectBoxStore.store
}
