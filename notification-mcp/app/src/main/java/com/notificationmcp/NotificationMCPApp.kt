package com.notificationmcp

import android.app.Application
import com.notificationmcp.ui.theme.PreferencesManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NotificationMCPApp : Application() {
    override fun onCreate() {
        super.onCreate()
        PreferencesManager.init(this)
    }
}
