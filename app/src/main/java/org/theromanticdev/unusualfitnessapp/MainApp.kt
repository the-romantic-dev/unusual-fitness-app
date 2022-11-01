package org.theromanticdev.unusualfitnessapp

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
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
val Context.screenWidthInDP: Int get() = run {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    var result = -1
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val metrics = windowManager.currentWindowMetrics;
        val insets = metrics.windowInsets.getInsetsIgnoringVisibility(
            WindowInsets.Type.navigationBars()
                    or WindowInsets.Type.displayCutout()
        )
        val insetsWidth = insets.right + insets.left
        val bounds = metrics.bounds
        result = bounds.width() - insetsWidth
    } else {
        @Suppress("DEPRECATION")
        result = windowManager.defaultDisplay.width
    }
    (result / resources.displayMetrics.density).toInt()
}