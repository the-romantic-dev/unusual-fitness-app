package org.theromanticdev.unusualfitnessapp

import android.app.Application
import android.content.Context
import org.theromanticdev.unusualfitnessapp.dagger.app.AppComponent
import org.theromanticdev.unusualfitnessapp.dagger.app.DaggerAppComponent


class MainApp: Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        appComponent = DaggerAppComponent.builder().context(this).build()
        super.onCreate()
    }


}

val Context.appComponent: AppComponent
    get() = when (this) {
        is MainApp -> appComponent
        else -> this.applicationContext.appComponent
    }