package org.theromanticdev.unusualfitnessapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat


val Context.screenWidthInDP: Int
    get() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val result: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = windowManager.currentWindowMetrics
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
        return (result / resources.displayMetrics.density).toInt()
    }

fun Context.getVectorResourceAsScaledBitmap(id: Int, width: Int, height: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(this, id)!!
    val bitmap = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, width, height)
    drawable.draw(canvas)

    return bitmap
}

fun Context.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}