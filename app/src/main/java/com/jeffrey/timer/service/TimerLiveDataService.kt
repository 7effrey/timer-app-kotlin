package com.jeffrey.timer.service

import androidx.lifecycle.LiveData

interface TimerLiveDataService {
    fun start(seconds: Long)
    fun pause()
    fun stop()

    val remainingSeconds: LiveData<Long>
    val remainingTime: LiveData<String>
    val runningStatus: LiveData<Boolean>
}