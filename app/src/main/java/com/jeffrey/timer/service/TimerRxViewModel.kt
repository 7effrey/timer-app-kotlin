package com.jeffrey.timer.service

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

interface TimerRxViewModel {
    fun set(viewEvent: ViewEvent): Disposable
    fun generateViewData(): ViewData

    interface ViewEvent {
        val start: Observable<Long>
        val pause: Observable<Unit>
        val stop: Observable<Unit>
    }

    interface ViewData {
        val remainingSeconds: Observable<Long>
        val remainingTime: Observable<String>
        val runningStatus: Observable<Boolean>
    }
}