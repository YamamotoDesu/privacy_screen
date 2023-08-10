package com.example.privacy_screen

import android.app.Activity
import android.content.Intent
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.view.WindowManager

class MainActivity: FlutterActivity() {
    private val CHANNEL = "security"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        setupMethodChannel(flutterEngine)
    }

    private fun setupMethodChannel(flutterEngine: FlutterEngine) {
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "enableAppSecurity" -> {
                    enableAppSecurity()
                    result.success(null)
                }
                "disableAppSecurity" -> {
                    disableAppSecurity()
                    result.success(null)
                }
                else -> result.notImplemented()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        toggleAppSecurity(hasFocus)
    }

    override fun onPause() {
        super.onPause()
        enableAppSecurity()
    }

    override fun onResume() {
        super.onResume()
        disableAppSecurity()
    }

    private fun toggleAppSecurity(hasFocus: Boolean) {
        if (hasFocus) {
            disableAppSecurity()
        } else {
            enableAppSecurity()
        }
    }

    private fun enableAppSecurity() {
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
    }
}
