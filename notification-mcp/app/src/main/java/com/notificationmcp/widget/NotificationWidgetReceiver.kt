package com.notificationmcp.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.notificationmcp.R
import com.notificationmcp.data.repository.NotificationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class NotificationWidgetReceiver : AppWidgetProvider() {

    @Inject lateinit var repository: NotificationRepository

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_initial)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val latest = repository.getRecentNotifications(1)
                val text = if (latest.isNotEmpty()) {
                    val notif = latest.first()
                    "${notif.appName}: ${notif.title ?: notif.content?.take(50) ?: "新通知"}"
                } else {
                    context.getString(R.string.widget_no_notifications)
                }
                views.setTextViewText(R.id.widget_text, text)
            } catch (e: Exception) {
                views.setTextViewText(R.id.widget_text, context.getString(R.string.widget_error))
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
