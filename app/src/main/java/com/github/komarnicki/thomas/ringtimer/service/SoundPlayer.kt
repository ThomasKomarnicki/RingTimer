package com.github.komarnicki.thomas.ringtimer.service

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import com.github.komarnicki.thomas.ringtimer.model.Timer

class SoundPlayer (timer: Timer, context: Context) {
    private val mediaPlayer : MediaPlayer = MediaPlayer.create(context, timer.soundClip)

    init {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION)
        mediaPlayer.prepareAsync()
    }

    fun playSoundClip(){
        mediaPlayer.start()
    }
}