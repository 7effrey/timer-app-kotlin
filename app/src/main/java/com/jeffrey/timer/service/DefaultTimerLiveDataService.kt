package com.jeffrey.timer.service

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.*
import com.jeffrey.timer.TestActivity
import com.jeffrey.timer.media.MediaPlayerUtils
import com.jeffrey.timer.notification.NotificationUtils.Companion.getNotificationManager
import com.jeffrey.timer.notification.NotificationUtils.Companion.updateNotification

class DefaultTimerLiveDataService  : Service(), TimerLiveDataService, LifecycleOwner {

    // Binder given to clients
    private val binder = TimerLiveDataBinder()

    private var vm: TimerLiveDataViewModel? = null
    private val mRemainingSeconds = MutableLiveData(0L)
    private val mRemainingTime = MutableLiveData("00:00")
    private val mRunningStatus = MutableLiveData(false)
    private val lifecycleRegistry = LifecycleRegistry(this)

    // region Service
    override fun onCreate() {
        super.onCreate()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        vm = DefaultTimerLiveDataViewModel()
        bindViewData()
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        val (id, notification) = updateNotification(applicationContext, "Title", "Text")
        startForeground(id, notification)
        if (vm?.runningStatus?.value != true)
            start(10)
        return START_NOT_STICKY
    }

    // endregion

    // region TimerViewModel
    private fun bindViewData() {

        vm?.runningStatus?.observe(this, Observer { mRunningStatus.postValue(it) })

        vm?.remainingSeconds?.observe(this, Observer { mRemainingSeconds.postValue(it) })

        vm?.remainingTime?.observe(this, Observer {
            applicationContext.apply {
                val (id, notification) = updateNotification(this, "Countdown Timer", it)
                getNotificationManager(this).notify(id, notification)
            }
            mRemainingTime.postValue(it)
        })

        vm?.isTimeUp?.observe(this, Observer { isTimeUp ->
            if (!isTimeUp)
                return@Observer
            val url = "https://www.pacdv.com/sounds/voices/maybe-next-time.wav"
            MediaPlayerUtils.playSound(url)

            vm?.resetIsTimeUp()

            startTestActivity()
        })
    }
    // endregion

    // region TimerService

    override val remainingSeconds: LiveData<Long> = mRemainingSeconds
    override val remainingTime: LiveData<String> = mRemainingTime
    override val runningStatus: LiveData<Boolean> = mRunningStatus

    override fun start(seconds: Long) {
        vm?.start(seconds)
    }
    override fun pause() {
        vm?.pause()
    }
    override fun stop() {
        vm?.stop()
    }
    // endregion

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class TimerLiveDataBinder : Binder() {
        // Return this instance of TimerForegroundService so clients can call public methods
        fun getService(): DefaultTimerLiveDataService = this@DefaultTimerLiveDataService
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    private fun startTestActivity() {
        val intent = Intent(applicationContext, TestActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}