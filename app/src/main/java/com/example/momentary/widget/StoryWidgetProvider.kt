package com.example.momentary.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.momentary.R

class StoryWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { id ->

            val intent = Intent(context, StoryWidgetService::class.java)

            val views = RemoteViews(context.packageName, R.layout.widget_story)
            @Suppress("DEPRECATION")
            views.setRemoteAdapter(R.id.stackView, intent)

            appWidgetManager.updateAppWidget(id, views)
        }
    }
}
