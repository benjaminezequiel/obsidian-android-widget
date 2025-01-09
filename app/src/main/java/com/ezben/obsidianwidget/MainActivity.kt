package com.ezben.obsidianwidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.SharedPreferences
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Keep your existing window insets code
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load saved vault name if it exists
        prefs = getSharedPreferences("ObsidianWidgetPrefs", MODE_PRIVATE)
        val vaultNameInput = findViewById<EditText>(R.id.vault_name_input)
        val currentVaultText = findViewById<TextView>(R.id.current_vault_text)

        updateCurrentVaultText(currentVaultText)

        findViewById<Button>(R.id.save_button).setOnClickListener {
            val vaultName = vaultNameInput.text.toString()
            if (vaultName.isNotEmpty()) {
                saveVaultName(vaultName)
                updateCurrentVaultText(currentVaultText)
                Toast.makeText(this, "Vault name saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a vault name", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.clear_button).setOnClickListener {
            vaultNameInput.setText("")
            saveVaultName("")
            updateCurrentVaultText(currentVaultText)
            Toast.makeText(this, "Vault name cleared!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveVaultName(vaultName: String) {
        prefs.edit()
            .putString("vault_name", vaultName)
            .apply()

        // Update all existing widgets
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val ids = appWidgetManager.getAppWidgetIds(
            ComponentName(this, ObsidianWidget::class.java)
        )
        for (widgetId in ids) {
            ObsidianWidget.updateAppWidget(this, appWidgetManager, widgetId)
        }
    }

    private fun updateCurrentVaultText(textView: TextView) {
        val currentVault = prefs.getString("vault_name", "") ?: ""
        if (currentVault.isEmpty()) {
            textView.text = "No vault configured"
        } else {
            textView.text = "Current vault: $currentVault"
        }
    }
}