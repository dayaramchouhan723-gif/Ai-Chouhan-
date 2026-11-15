package com.example.util

import android.content.Context
import android.content.Intent
import com.example.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.recaptcha.RecaptchaAppCheckProviderFactory
import com.google.firebase.initialize

fun configureAppCheck(context: Context, intent: Intent? = null) {
    try {
        Firebase.initialize(context)
    } catch (_: Exception) {
    }

    try {
        if (BuildConfig.DEBUG) {
            val prefs = context.getSharedPreferences(
                "com.google.firebase.appcheck.debug.store.${context.packageName}",
                Context.MODE_PRIVATE
            )
            val token = intent?.getStringExtra("FIREBASE_APPCHECK_DEBUG_TOKEN")
                ?: try { BuildConfig::class.java.getField("FIREBASE_APPCHECK_DEBUG_TOKEN").get(null) as? String } catch (_: Exception) { null }
                ?: "ai-studio-debug-token"
            prefs.edit().putString("debug_secret", token).commit()
            Firebase.appCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            val resId = context.resources.getIdentifier("recaptcha_site_key", "string", context.packageName)
            val siteKey = if (resId != 0) context.getString(resId) else ""
            if (siteKey.isNotEmpty()) {
                Firebase.appCheck.installAppCheckProviderFactory(
                    RecaptchaAppCheckProviderFactory.getInstance(siteKey)
                )
            }
        }
    } catch (_: Exception) {
        // Non-fatal in unit tests / offline Robolectric environments
    }
}
