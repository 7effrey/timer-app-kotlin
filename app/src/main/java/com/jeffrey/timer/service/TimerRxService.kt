package com.jeffrey.timer.service

import io.reactivex.rxjava3.core.Observable

interface TimerRxService {
    val remainingSeconds: Observable<Long>
    val remainingTime: Observable<String>
    val runningStatus: Observable<Boolean>

    fun start(seconds: Long)
    fun pause()
    fun stop()
}