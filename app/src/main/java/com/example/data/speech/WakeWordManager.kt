package com.example.data.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

interface WakeWordListener {
    fun onWakeWordDetected(matchedPhrase: String, trailingCommand: String?)
    fun onRmsChanged(rmsDb: Float)
    fun onListeningStateChanged(isListening: Boolean)
    fun onSpeechRecognized(text: String)
    fun onError(errorMessage: String)
}

/**
 * High-performance, resilient on-device Wake Word detection engine for "Hey Myra".
 * Utilizes Android SpeechRecognizer with automatic continuous loop recovery,
 * partial result analysis, and trailing command extraction.
 */
class WakeWordManager(
    private val context: Context,
    private val listener: WakeWordListener
) {
    private val tag = "WakeWordManager"
    private val mainHandler = Handler(Looper.getMainLooper())

    private var speechRecognizer: SpeechRecognizer? = null
    private var isContinuousListeningEnabled = false
    private var isCurrentlyListening = false
    private var lastTriggerTimestamp = 0L

    // Wake word variants in Hinglish & English
    private val wakeWords = listOf(
        "hey myra",
        "hi myra",
        "hay myra",
        "he myra",
        "sun myra",
        "namaste myra",
        "ok myra",
        "okay myra",
        "hey mira",
        "hi mira",
        "myra",
        "mira"
    )

    private val recognitionIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-IN")
        putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
    }

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            isCurrentlyListening = true
            listener.onListeningStateChanged(true)
        }

        override fun onBeginningOfSpeech() {
            // User began speaking
        }

        override fun onRmsChanged(rmsdB: Float) {
            val normalized = if (rmsdB < 0f) 0f else rmsdB
            listener.onRmsChanged(normalized)
        }

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            isCurrentlyListening = false
            listener.onListeningStateChanged(false)
            scheduleRestart(250)
        }

        override fun onError(error: Int) {
            isCurrentlyListening = false
            listener.onListeningStateChanged(false)
            val message = getErrorText(error)
            Log.d(tag, "SpeechRecognizer error: $error ($message)")

            // Restart listening if continuous mode is enabled
            if (isContinuousListeningEnabled) {
                val delay = when (error) {
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> 600L
                    SpeechRecognizer.ERROR_NO_MATCH, SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> 200L
                    else -> 800L
                }
                scheduleRestart(delay)
            }
        }

        override fun onResults(results: Bundle?) {
            isCurrentlyListening = false
            listener.onListeningStateChanged(false)
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val best = matches[0]
                listener.onSpeechRecognized(best)
                processHypotheses(matches)
            }

            if (isContinuousListeningEnabled) {
                scheduleRestart(300)
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val partials = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!partials.isNullOrEmpty()) {
                val best = partials[0]
                listener.onSpeechRecognized(best)
                processHypotheses(partials)
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun processHypotheses(hypotheses: List<String>) {
        val now = System.currentTimeMillis()
        if (now - lastTriggerTimestamp < 2200L) {
            // Debounce to prevent multi-fire on same utterance
            return
        }

        for (hypothesis in hypotheses) {
            val lower = hypothesis.lowercase(Locale.ROOT).trim()
            for (wakeWord in wakeWords) {
                val index = lower.indexOf(wakeWord)
                if (index != -1) {
                    lastTriggerTimestamp = now
                    val trailing = lower.substring(index + wakeWord.length).trim().trim(',', '.', '!', '?')
                    val command = if (trailing.isNotBlank()) trailing else null
                    Log.i(tag, "Wake word triggered: '$wakeWord' with command: '$command'")
                    listener.onWakeWordDetected(wakeWord, command)
                    return
                }
            }
        }
    }

    fun startListening() {
        mainHandler.post {
            isContinuousListeningEnabled = true
            ensureRecognizer()
            safeStartListening()
        }
    }

    fun stopListening() {
        mainHandler.post {
            isContinuousListeningEnabled = false
            mainHandler.removeCallbacksAndMessages(null)
            try {
                speechRecognizer?.stopListening()
                speechRecognizer?.cancel()
            } catch (e: Exception) {
                Log.w(tag, "Error stopping recognizer", e)
            }
            isCurrentlyListening = false
            listener.onListeningStateChanged(false)
        }
    }

    fun simulateWakeWord(phrase: String = "Hey Myra", trailingCommand: String? = null) {
        mainHandler.post {
            listener.onSpeechRecognized("$phrase ${trailingCommand ?: ""}".trim())
            listener.onWakeWordDetected(phrase, trailingCommand)
        }
    }

    private fun ensureRecognizer() {
        if (speechRecognizer == null) {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                try {
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                        setRecognitionListener(recognitionListener)
                    }
                } catch (e: Exception) {
                    Log.e(tag, "Failed to instantiate SpeechRecognizer", e)
                    listener.onError("Speech recognition service unavailable on this device")
                }
            } else {
                Log.w(tag, "Speech recognition not available on device")
                listener.onError("Device Speech Recognition service not available")
            }
        }
    }

    private fun safeStartListening() {
        if (!isContinuousListeningEnabled) return
        try {
            ensureRecognizer()
            speechRecognizer?.startListening(recognitionIntent)
            isCurrentlyListening = true
            listener.onListeningStateChanged(true)
        } catch (e: Exception) {
            Log.e(tag, "Error in safeStartListening", e)
            scheduleRestart(1000)
        }
    }

    private fun scheduleRestart(delayMs: Long) {
        if (!isContinuousListeningEnabled) return
        mainHandler.removeCallbacksAndMessages(null)
        mainHandler.postDelayed({
            if (isContinuousListeningEnabled && !isCurrentlyListening) {
                safeStartListening()
            }
        }, delayMs)
    }

    fun destroy() {
        mainHandler.post {
            isContinuousListeningEnabled = false
            mainHandler.removeCallbacksAndMessages(null)
            try {
                speechRecognizer?.destroy()
            } catch (e: Exception) {
                Log.w(tag, "Error destroying speechRecognizer", e)
            }
            speechRecognizer = null
            isCurrentlyListening = false
        }
    }

    private fun getErrorText(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient audio permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Speech recognition error code $errorCode"
        }
    }
}
