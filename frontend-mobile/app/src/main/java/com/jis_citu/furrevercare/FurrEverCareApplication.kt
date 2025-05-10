package com.jis_citu.furrevercare
import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FurrEverCareApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}