package com.ezben.obsidianwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.app.PendingIntent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast

class ObsidianWidget : AppWidgetProvider() {
    companion object {
        private const val TAG = "ObsidianWidget"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            Log.d(TAG, "updateAppWidget called for widgetId: $appWidgetId")

            try {
                // Create RemoteViews
                val views = RemoteViews(context.packageName, R.layout.obsidian_widget)
                Log.d(TAG, "RemoteViews created successfully")

                // Retrieve vault name
                val prefs = context.getSharedPreferences("ObsidianWidgetPrefs", Context.MODE_PRIVATE)
                val vaultName = prefs.getString("vault_name", "") ?: ""
                Log.d(TAG, "Vault name retrieved: '$vaultName'")

                // Configure visibility based on vault name
                if (vaultName.isEmpty()) {
                    Log.d(TAG, "No vault name set, showing configuration message")
                    configureEmptyState(views)
                } else {
                    Log.d(TAG, "Vault name set, configuring widget buttons")
                    configureButtonsWithVaultName(context, views, vaultName)
                }

                // Update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views)
                Log.d(TAG, "Widget $appWidgetId updated successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error in updateAppWidget", e)
                Toast.makeText(context, "Widget update error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        private fun configureEmptyState(views: RemoteViews) {
            views.setViewVisibility(R.id.no_vault_message, View.VISIBLE)
            views.setViewVisibility(R.id.new_note_button, View.GONE)
            views.setViewVisibility(R.id.daily_note_button, View.GONE)
            views.setViewVisibility(R.id.search_vault_button, View.GONE)
        }

        private fun configureButtonsWithVaultName(context: Context, views: RemoteViews, vaultName: String) {
            views.setViewVisibility(R.id.no_vault_message, View.GONE)
            views.setViewVisibility(R.id.new_note_button, View.VISIBLE)
            views.setViewVisibility(R.id.daily_note_button, View.VISIBLE)
            views.setViewVisibility(R.id.search_vault_button, View.VISIBLE)

            // Configure intents for each button
            val buttonConfigs = listOf(
                Pair(R.id.new_note_button, "obsidian://new?vault=$vaultName"),
                Pair(R.id.daily_note_button, "obsidian://daily?vault=$vaultName"),
                Pair(R.id.search_vault_button, "obsidian://search?vault=$vaultName")
            )

            buttonConfigs.forEach { (buttonId, uriString) ->
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(uriString)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

                val pendingIntent = PendingIntent.getActivity(
                    context,
                    buttonId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                views.setOnClickPendingIntent(buttonId, pendingIntent)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d(TAG, "onUpdate called for ${appWidgetIds.size} widget(s)")

        // Ensure each widget is updated
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive called with action: ${intent.action}")
        super.onReceive(context, intent)
    }

    override fun onEnabled(context: Context) {
        Log.d(TAG, "Widget provider enabled")
    }

    override fun onDisabled(context: Context) {
        Log.d(TAG, "Last widget disabled")
    }
}