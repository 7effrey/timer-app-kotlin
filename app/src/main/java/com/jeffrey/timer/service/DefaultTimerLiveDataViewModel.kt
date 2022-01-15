package com.jeffrey.timer.service

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jeffrey.timer.extension.toMinuteSecondFormat

class DefaultTimerLiveDataViewModel : ViewModel(), TimerLiveDataViewModel {

    private val mRemainingSeconds = MutableLiveData<Long>()
    private val mRemainingTime = MutableLiveData<String>()
    private val mRunningStatus = MutableLiveData<Boolean>()
    private val mIsTimeUp = MutableLiveData(false)
    private var countDownTimer: CountDownTimer? = null

    override fun start(time: Long) {
        mRunningStatus.postValue(true)
        updateTime(time)
        countDownTimer?.cancel()
        countDownTimer = object: CountDownTimer(time * 1000, 1000) {
            override fun onTick(newSecondsInMillis: Long) {
                val newSeconds = newSecondsInMillis / 1000
                updateTime(newSeconds)
            }

            override fun onFinish() {
                mRunningStatus.postValue(false)
                mIsTimeUp.postValue(true)
            }
        }
        countDownTimer?.start()
    }

    override fun pause() {
        mRunningStatus.postValue(false)
        countDownTimer?.cancel()
    }

    override fun stop() {
        mRunningStatus.postValue(false)
        countDownTimer?.cancel()
        updateTime(0)
    }

    override fun resetIsTimeUp() {
        mIsTimeUp.postValue(false)
    }

    override val remainingSeconds: LiveData<Long> = mRemainingSeconds
    override val remainingTime: LiveData<String> = mRemainingTime
    override val runningStatus: LiveData<Boolean> = mRunningStatus
    override val isTimeUp: LiveData<Boolean> = mIsTimeUp

    private fun updateTime(seconds: Long) {
        mRemainingSeconds.postValue(seconds)
        val text = seconds.toMinuteSecondFormat()
        mRemainingTime.postValue(text)
    }
}