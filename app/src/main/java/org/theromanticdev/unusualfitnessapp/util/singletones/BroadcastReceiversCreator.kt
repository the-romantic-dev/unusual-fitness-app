package org.theromanticdev.unusualfitnessapp.util.singletones

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

object BroadcastReceiversCreator {

    fun create(listener: (Context, Intent) -> Unit): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                listener(context, intent)
            }

        }
    }
}