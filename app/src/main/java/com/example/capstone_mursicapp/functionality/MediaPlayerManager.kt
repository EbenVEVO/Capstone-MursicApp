package com.example.capstone_mursicapp.functionality

import android.media.AudioAttributes
import android.media.MediaPlayer

object MediaPlayerManager {
    private var mediaPlayer: MediaPlayer? = null

    fun playAudio(previewUrl: String) {
        releaseMediaPlayer()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setDataSource(previewUrl)
            prepare()
            start()
        }
    }

    fun pauseAudio() {
        mediaPlayer?.pause()
    }

    fun stopAudio() {
        mediaPlayer?.stop()
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        mediaPlayer?.setOnCompletionListener(listener)
    }
}