package com.example.service

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.data.model.AvatarEmotionState
import com.example.data.model.SanamCatalog
import com.example.data.model.VoiceProfileItem
import java.util.Locale

/**
 * Sovereign Voice Pipeline:
 * - Streaming Android TextToSpeech with default Hindi Female pitch 1.2 / rate 0.9
 * - 32 Voice Profiles across Hindi, English, Urdu, Sindhi, Dhatki + Expressive moods
 * - Pocket TTS / Pocket TTS-Real / Pocket TTS-Hindi engine mode support
 * - Interrupt / Barge-In: Immediately stops speaking if user begins talking
 * - Live Human Engine emotion analyzer for natural avatar facial/body transitions
 */
class VoiceService(
    context: Context,
    private val onSpeakingStateChanged: (Boolean) -> Unit
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = try {
        TextToSpeech(context.applicationContext, this)
    } catch (_: Exception) {
        null
    }

    @Volatile
    private var isReady = false

    @Volatile
    var isSpeakingNow: Boolean = false
        private set

    var activeVoiceId: String = "hi_f_1"
    var pitch: Float = 1.2f
    var rate: Float = 0.9f
    var pocketEngineMode: String = "Pocket TTS-Hindi"
    var multiSpeakerEnabled: Boolean = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isReady = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    isSpeakingNow = true
                    onSpeakingStateChanged(true)
                }

                override fun onDone(utteranceId: String?) {
                    isSpeakingNow = false
                    onSpeakingStateChanged(false)
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    isSpeakingNow = false
                    onSpeakingStateChanged(false)
                }
            })
            applyVoiceProfile(activeVoiceId, pitch, rate)
        }
    }

    fun applyVoiceProfile(voiceId: String, customPitch: Float? = null, customRate: Float? = null) {
        val profile: VoiceProfileItem? = SanamCatalog.thirtyTwoVoices.find { it.id == voiceId }
        activeVoiceId = profile?.id ?: voiceId
        pitch = customPitch ?: profile?.pitch ?: 1.2f
        rate = customRate ?: profile?.rate ?: 0.9f
        val localeTag = profile?.localeTag ?: "hi-IN"
        try {
            val parts = localeTag.split("-")
            val locale = if (parts.size >= 2) Locale(parts[0], parts[1]) else Locale("hi", "IN")
            val res = tts?.setLanguage(locale)
            if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale("hi", "IN"))
            }
            tts?.setPitch(pitch.coerceIn(0.5f, 2.0f))
            tts?.setSpeechRate(rate.coerceIn(0.5f, 2.0f))
        } catch (_: Exception) {
        }
    }

    fun speak(text: String, emotion: AvatarEmotionState = AvatarEmotionState.NEUTRAL) {
        if (text.isBlank()) return
        val emotionalPitch = when (emotion) {
            AvatarEmotionState.SHY_BLUSH, AvatarEmotionState.ROMANTIC -> (pitch + 0.08f).coerceAtMost(1.8f)
            AvatarEmotionState.LAUGHING, AvatarEmotionState.HAPPY -> (pitch + 0.12f).coerceAtMost(1.9f)
            AvatarEmotionState.ROAST_ANGRY -> (pitch - 0.10f).coerceAtLeast(0.7f)
            AvatarEmotionState.SAD_CARING -> (pitch - 0.05f).coerceAtLeast(0.75f)
            else -> pitch
        }
        val emotionalRate = when (emotion) {
            AvatarEmotionState.ROMANTIC, AvatarEmotionState.SAD_CARING -> (rate - 0.08f).coerceAtLeast(0.65f)
            AvatarEmotionState.ROAST_ANGRY, AvatarEmotionState.SURPRISED -> (rate + 0.10f).coerceAtMost(1.5f)
            else -> rate
        }

        if (isReady) {
            try {
                tts?.setPitch(emotionalPitch)
                tts?.setSpeechRate(emotionalRate)
                val params = Bundle().apply {
                    putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
                }
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "ZaraVoice_${System.currentTimeMillis()}")
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Interrupt / Barge-In: If user speaks while ZARA is talking, cut TTS instantly and resume listening.
     */
    fun interruptForBargeIn() {
        try {
            if (isSpeakingNow) {
                tts?.stop()
                isSpeakingNow = false
                onSpeakingStateChanged(false)
            }
        } catch (_: Exception) {
        }
    }

    fun stopSpeaking() {
        try {
            tts?.stop()
        } catch (_: Exception) {
        }
        isSpeakingNow = false
        onSpeakingStateChanged(false)
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {
        }
    }

    companion object {
        fun detectEmotionForLiveHumanEngine(userInput: String, replyText: String = "", moodTheme: String = ""): AvatarEmotionState {
            val combined = "$userInput $replyText".lowercase(Locale.ROOT)
            return when {
                listOf("love", "pyar", "sharma", "blush", "dil", "jaan").any { it in combined } -> AvatarEmotionState.SHY_BLUSH
                listOf("bye", "hi", "hello", "namaste", "salam", "wake up", "suno").any { it in userInput.lowercase(Locale.ROOT) } -> AvatarEmotionState.WAVING
                listOf("haha", "laugh", "funny", "mazak", "joke").any { it in combined } -> AvatarEmotionState.LAUGHING
                listOf("roast", "sassy", "attitude", "gussa", "khabardar", "galat").any { it in combined } -> AvatarEmotionState.ROAST_ANGRY
                listOf("sad", "udaas", "thak", "paani", "aaram", "chinta").any { it in combined } -> AvatarEmotionState.SAD_CARING
                listOf("wow", "kya baat", "sach", "amazing", "ban gaya", "passed").any { it in combined } -> AvatarEmotionState.SURPRISED
                listOf("wink", "secret", "chup").any { it in combined } -> AvatarEmotionState.WINK
                "sassy" in moodTheme.lowercase(Locale.ROOT) -> AvatarEmotionState.ROAST_ANGRY
                "romantic" in moodTheme.lowercase(Locale.ROOT) -> AvatarEmotionState.ROMANTIC
                "funny" in moodTheme.lowercase(Locale.ROOT) -> AvatarEmotionState.HAPPY
                else -> AvatarEmotionState.HAPPY
            }
        }
    }
}
