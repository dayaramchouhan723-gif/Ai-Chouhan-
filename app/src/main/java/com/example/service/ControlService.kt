package com.example.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.Settings
import com.example.data.model.ActionConfirmationRequest
import java.util.Locale

/**
 * Hardware, App Launcher & Safety Guard Control Service.
 * Strictly routes Pay / Send / Delete actions through an explicit user Confirmation Card before executing.
 */
class ControlService(private val context: Context) {

    fun detectSensitiveConfirmation(command: String): ActionConfirmationRequest? {
        val lower = command.lowercase(Locale.ROOT)
        return when {
            listOf("pay ", "upi", "transfer", "paise bhejo", "send money").any { it in lower } ->
                ActionConfirmationRequest(
                    id = "conf_${System.currentTimeMillis()}",
                    actionType = "PAY",
                    summary = "Payment / Transfer request: \"$command\"\nCHOUHAN STAR kabhi bina aapki permission ke paise transfer nahi karega.",
                    targetLabel = "Payment Shield",
                    payload = command
                )
            listOf("delete all", "erase", "remove all", "format", "hatao", "delete ").any { it in lower } ->
                ActionConfirmationRequest(
                    id = "conf_${System.currentTimeMillis()}",
                    actionType = "DELETE",
                    summary = "Delete action detected: \"$command\"\nConfirm karein tabhi data delete hoga.",
                    targetLabel = "Data Guard",
                    payload = command
                )
            listOf("send email", "send sms", "bhej do", "mail bhejo").any { it in lower } ->
                ActionConfirmationRequest(
                    id = "conf_${System.currentTimeMillis()}",
                    actionType = "SEND",
                    summary = "External message / email draft ready: \"$command\"\nAllow dabane par aapka Mail/SMS app draft khulega.",
                    targetLabel = "Message Draft",
                    payload = command
                )
            else -> null
        }
    }

    fun launchAppByName(name: String): Pair<Boolean, String> {
        val lower = name.lowercase(Locale.ROOT)
        val pkg = when {
            "whatsapp" in lower -> "com.whatsapp"
            "youtube" in lower -> "com.google.android.youtube"
            "spotify" in lower -> "com.spotify.music"
            "chrome" in lower -> "com.android.chrome"
            "gmail" in lower || "mail" in lower -> "com.google.android.gm"
            "calendar" in lower -> "com.google.android.calendar"
            "map" in lower -> "com.google.android.apps.maps"
            "settings" in lower -> {
                openIntentSafely(Intent(Settings.ACTION_SETTINGS))
                return true to "Opening Android Settings"
            }
            else -> null
        }

        if (pkg != null) {
            val launch = context.packageManager.getLaunchIntentForPackage(pkg)
            if (launch != null && openIntentSafely(launch)) {
                return true to "Launched $name cleanly"
            }
        }
        return false to "$name app ready (install $name to auto-launch externally)"
    }

    fun openDialer(phoneNumber: String = ""): Boolean {
        val uri = if (phoneNumber.isBlank()) Uri.parse("tel:") else Uri.parse("tel:$phoneNumber")
        return openIntentSafely(Intent(Intent.ACTION_DIAL, uri))
    }

    fun openSmsDraft(recipient: String, body: String): Boolean {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${recipient.ifBlank { "" }}")).apply {
            putExtra("sms_body", body)
        }
        return openIntentSafely(intent)
    }

    fun openEmailDraft(recipient: String, subject: String, body: String): Boolean {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${recipient.ifBlank { "" }}")).apply {
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        return openIntentSafely(intent)
    }

    fun openCalendarEventDraft(title: String, description: String): Boolean {
        val startMs = System.currentTimeMillis() + 3_600_000L
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.Events.DESCRIPTION, description)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMs)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, startMs + 1_800_000L)
        }
        return openIntentSafely(intent)
    }

    fun setSystemAlarm(hour: Int, minute: Int, message: String): Boolean {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, message)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
        }
        return openIntentSafely(intent)
    }

    fun openAccessibilitySettings(): Boolean {
        return openIntentSafely(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    fun openWebUrl(url: String): Boolean {
        return openIntentSafely(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private fun openIntentSafely(intent: Intent): Boolean {
        return try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }
}
