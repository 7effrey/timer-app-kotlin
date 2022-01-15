package com.jeffrey.timer.service

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.*
import com.jeffrey.timer.notification.NotificationUtils.Companion.getNotificationManager
import com.jeffrey.timer.notification.NotificationUtils.Companion.updateNotification
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*

class DefaultTimerRxService : Service(), TimerRxService {

    // Binder given to clients
    private val binder = TimerRxBinder()

    private var viewModel: TimerRxViewModel? = null
    private val veStart = PublishSubject.create<Long>()
    private val vePause = PublishSubject.create<Unit>()
    private val veStop = PublishSubject.create<Unit>()
    private val _remainingSeconds = BehaviorSubject.createDefault(0L)
    private val _remainingTime = BehaviorSubject.createDefault("00:00")
    private val _runningStatus = BehaviorSubject.createDefault(false)
    private var disposables: CompositeDisposable = CompositeDisposable()

    // region Service
    override fun onCreate() {
        super.onCreate()
        viewModel = DefaultTimerRxViewModel()
        bindViewData()
        setViewModelViewEvent()
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val (id, notification) = updateNotification(applicationContext, "Title", "Text")
        startForeground(id, notification)
        start(30)
        return START_NOT_STICKY
    }

    // endregion

    // region TimerViewModel
    private fun bindViewData() {
        val viewData = viewModel?.generateViewData() ?: return

        disposables.add(
            viewData.remainingSeconds.subscribe { _remainingSeconds.onNext(it) }
        )

        disposables.add(
            viewData.remainingTime.subscribe {
                applicationContext.apply {
                    val (id, notification) = updateNotification(this, "Countdown Timer", it)
                    getNotificationManager(this).notify(id, notification)
                }
                _remainingTime.onNext(it)
            }
        )

        disposables.add(
            viewData.runningStatus.subscribe { _runningStatus.onNext(it) }
        )
    }

    private fun setViewModelViewEvent() {
        val viewEvent = object : TimerRxViewModel.ViewEvent {
            override val start: Observable<Long> = veStart
            override val pause: Observable<Unit> = vePause
            override val stop: Observable<Unit> = veStop
        }
        viewModel?.set(viewEvent)?.let { disposables.add(it) }
    }
    // endregion

    // region TimerService
    override val remainingSeconds: Observable<Long> = _remainingSeconds
    override val remainingTime: Observable<String> = _remainingTime
    override val runningStatus: Observable<Boolean> = _runningStatus

    override fun start(seconds: Long) {
        veStart.onNext(seconds)
    }
    override fun pause() {
        vePause.onNext(Unit)
    }
    override fun stop() {
        veStop.onNext(Unit)
    }
    // endregion

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class TimerRxBinder : Binder() {
        // Return this instance of TimerForegroundService so clients can call public methods
        fun getService(): DefaultTimerRxService = this@DefaultTimerRxService
    }
}

