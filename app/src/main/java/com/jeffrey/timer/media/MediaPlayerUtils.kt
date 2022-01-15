package com.jeffrey.timer.media

import android.media.AudioAttributes
import android.media.MediaPlayer

class MediaPlayerUtils {
    companion object {
        fun playSound(url: String) {
            MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                prepare() // might take long! (for buffering, etc)
                start()
                setOnCompletionListener {
                    it.release()
                }
            }
        }
    }
}