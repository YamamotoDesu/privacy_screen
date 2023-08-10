# [privacy_screen](https://articles.wesionary.team/securing-your-flutter-app-implementing-a-privacy-screen-61383ce09f0a)
<img width="300" alt="image" src="https://github.com/YamamotoDesu/privacy_screen/assets/47273077/be9f5d81-308e-42e4-a1c0-a7443f4490ea">

AppDelegate.swift
```swift
import UIKit
import Flutter

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  private var flutterViewController: FlutterViewController!
  private var securityChannel: FlutterMethodChannel!
  private var blurEffectView: UIVisualEffectView?
  private var isInBackground: Bool = false // Track whether app is in background
  
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    GeneratedPluginRegistrant.register(with: self)
    
    setupFlutterCommunication()

    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }

  private func setupFlutterCommunication() {
    flutterViewController = window?.rootViewController as? FlutterViewController
    securityChannel = FlutterMethodChannel(
      name: "security",
      binaryMessenger: flutterViewController.binaryMessenger
    )

    securityChannel.setMethodCallHandler(handle)
  }

  override func applicationDidEnterBackground(_ application: UIApplication) {
    isInBackground = true // App entered background
    enableAppSecurity()
  }

  override func applicationDidBecomeActive(_ application: UIApplication) {
    // Check if the app was in background before becoming active
    if isInBackground {
      disableAppSecurity()
      isInBackground = false
    }
  }

  private func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "enableAppSecurity":
      result(nil)
    case "disableAppSecurity":
      result(nil)
    default:
      result(FlutterMethodNotImplemented)
    }
  }

  private func enableAppSecurity() {
    let blurEffect = UIBlurEffect(style: .light)
    blurEffectView = UIVisualEffectView(effect: blurEffect)
    blurEffectView?.frame = window!.frame
    window?.addSubview(blurEffectView!)
  }

  private func disableAppSecurity() {
    blurEffectView?.removeFromSuperview()
  }
}
```

MainActivity.kt
```kt
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
```

lib/platdorm_channel_util.dart
```dart
import 'dart:developer';

import 'package:flutter/services.dart';

abstract class IAppScreenPrivacy {
  Future<void> enableScreenPrivacy();
  Future<void> disableScreenPrivacy();
}

class AppScreenPrivacyService extends IAppScreenPrivacy {
  static const platform = MethodChannel('security');
  @override
  Future<void> disableScreenPrivacy() async {
    try {
      await platform.invokeMethod('disableAppSecurity');
    } on PlatformException   catch (e) {
      log('Failed to disable app security: "${e.message}"');
    }
  }

  @override
  Future<void> enableScreenPrivacy() async {
    try {
      await platform.invokeMethod('enableAppSecurity');
    } on PlatformException catch (e) {
      log('Failed to enable app security: "${e.message}"');
    }
  }
}

```
