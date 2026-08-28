package com.lz.telefonoprolz

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.lz.telefonoprolz.util.ThemeHelper

class TelefonoProApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ThemeHelper.applyStoredTheme(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_CALLS,
                getString(R.string.notif_channel_calls),
                NotificationManager.IMPORTANCE_HIGH
            )
            manager?.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_CALLS = "telefono_pro_calls"
    }
}
