package com.jeffrey.timer.service

import androidx.lifecycle.LiveData

interface TimerLiveDataViewModel {
    fun start(time: Long)
    fun pause()
    fun stop()
    fun resetIsTimeUp()

    val remainingSeconds: LiveData<Long>
    val remainingTime: LiveData<String>
    val runningStatus: LiveData<Boolean>
    val isTimeUp: LiveData<Boolean>
}