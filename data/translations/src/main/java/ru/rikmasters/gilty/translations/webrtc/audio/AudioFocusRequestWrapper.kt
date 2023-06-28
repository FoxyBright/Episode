package ru.rikmasters.gilty.translations.webrtc.audio

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager

/**
 * [AudioFocusRequestWrapper]  allows to listen for changes in audio focus.
 * [AudioAttributes] - USAGE - определяет тип использования звука, такой как музыка, речь, уведомления,
 * CONTENT_TYPE - определяет тип контента, который воспроизводится, такой как музыка, фильмы, игры,
 * FLAGS - определяет дополнительные флаги для аудиофайла, такие как использование медиакнопок на гарнитуре или регулирование громкости звука,
 * LEGACY_STREAM_TYPE - определяет тип потока звука для совместимости со старыми устройствами
 */
internal class AudioFocusRequestWrapper {
    @SuppressLint("NewApi")
    fun buildRequest(audioFocusChangeListener: AudioManager.OnAudioFocusChangeListener): AudioFocusRequest {
        val playbackAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
            .build()
        return AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            .setAudioAttributes(playbackAttributes)
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener(audioFocusChangeListener)
            .build()
    }
}