package com.jeffrey.timer.service

import com.jeffrey.timer.extension.toMinuteSecondFormat
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class DefaultTimerRxViewModel(
    private val uiScheduler: Scheduler = AndroidSchedulers.mainThread(),
    private val ioScheduler: Scheduler = Schedulers.io()
) : TimerRxViewModel {

    private var disposables = CompositeDisposable()
    private var timerDisposable: Disposable? = null

    private val vdRemainingSeconds = BehaviorSubject.createDefault(0L)
    private val vdRemainingTime = BehaviorSubject.createDefault("00:00")
    private val vdRunningStatus = BehaviorSubject.createDefault(false)

    override fun set(viewEvent: TimerRxViewModel.ViewEvent): Disposable {
        disposables.add(viewEvent.start
            .subscribe { seconds ->
                vdRunningStatus.onNext(true)
                timerDisposable?.dispose()
                updateTime(seconds)

                timerDisposable = Observable.interval(1, TimeUnit.SECONDS)
                    .subscribeOn(ioScheduler)
                    .observeOn(uiScheduler)
                    .subscribe {
                        val newSeconds = (vdRemainingSeconds.value ?: 0) - 1
                        updateTime(newSeconds)
                        if (newSeconds <= 0) {
                            vdRunningStatus.onNext(false)
                            timerDisposable?.dispose()
                        }
                    }.apply { disposables.add(this) }
            })

        disposables.add(viewEvent.pause
            .subscribe {
                vdRunningStatus.onNext(false)
                timerDisposable?.dispose()
            })

        disposables.add(viewEvent.stop
            .subscribe {
                vdRunningStatus.onNext(false)
                timerDisposable?.dispose()
                updateTime(0)
            })

        return disposables
    }

    private fun updateTime(seconds: Long) {
        vdRemainingSeconds.onNext(seconds)
        val text = seconds.toMinuteSecondFormat()
        vdRemainingTime.onNext(text)
    }

    override fun generateViewData(): TimerRxViewModel.ViewData {
        return object: TimerRxViewModel.ViewData {
            override val remainingSeconds: Observable<Long> = vdRemainingSeconds
            override val remainingTime: Observable<String> = vdRemainingTime
            override val runningStatus: Observable<Boolean> = vdRunningStatus
        }
    }
}