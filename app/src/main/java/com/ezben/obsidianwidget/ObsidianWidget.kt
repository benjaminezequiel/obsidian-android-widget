package com.ezben.obsidianwidget

import android.widget.Toast
import android.util.Log
import android.content.Intent
import android.net.Uri
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.view.View
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class ObsidianWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {

            // Get the vault name from SharedPreferences
            val vaultName = context.getSharedPreferences("ObsidianWidgetPrefs", Context.MODE_PRIVATE)
                .getString("vault_name", "") ?: ""

            val views = RemoteViews(context.packageName, R.layout.obsidian_widget)

//            views.setImageViewResource(R.id.open_vault_button, R.drawable.outline_add_circle_24)


            if (vaultName.isEmpty()) {
                views.setViewVisibility(R.id.no_vault_message, View.VISIBLE)
                views.setViewVisibility(R.id.open_vault_button, View.GONE)
                views.setViewVisibility(R.id.new_note_button, View.GONE)
                views.setViewVisibility(R.id.daily_note_button, View.GONE)
                views.setViewVisibility(R.id.search_vault_button, View.GONE)
            } else {
                views.setViewVisibility(R.id.no_vault_message, View.GONE)
                views.setViewVisibility(R.id.open_vault_button, View.VISIBLE)
                views.setViewVisibility(R.id.new_note_button, View.VISIBLE)
                views.setViewVisibility(R.id.daily_note_button, View.VISIBLE)
                views.setViewVisibility(R.id.search_vault_button, View.VISIBLE)

                val openVault = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("obsidian://open?vault=$vaultName")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

                val newNote = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("obsidian://new?vault=$vaultName")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

                val dailyNote = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("obsidian://daily?vault=$vaultName")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

                val searchVault = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("obsidian://search?vault=$vaultName")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

                try {
                    val openVaultPendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        openVault,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    val newNotePendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        newNote,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    val dailyNotePendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        dailyNote,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    val searchVaultPendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        searchVault,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    views.setOnClickPendingIntent(R.id.new_note_button, newNotePendingIntent)
                    views.setOnClickPendingIntent(R.id.open_vault_button, openVaultPendingIntent)
                    views.setOnClickPendingIntent(R.id.daily_note_button, dailyNotePendingIntent)
                    views.setOnClickPendingIntent(R.id.search_vault_button, searchVaultPendingIntent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }


    override fun onReceive(context: Context, intent: Intent) {
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
