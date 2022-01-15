package com.jeffrey.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.jeffrey.timer.service.DefaultTimerLiveDataService
import com.jeffrey.timer.service.DefaultTimerRxService
import com.jeffrey.timer.service.TimerLiveDataService
import com.jeffrey.timer.service.TimerRxService

class MainActivity : AppCompatActivity() {

    private var timerRxService: TimerRxService? = null
    private var timerLiveDataService: TimerLiveDataService? = null
    private var timerServiceBound: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = getTimerServiceIntent()
        startService(intent)
        if (!timerServiceBound) {
            bindService(intent, timerServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onDestroy() {
        unbindService(timerServiceConnection)
        super.onDestroy()
    }

    private fun getTimerServiceIntent(): Intent {
        return Intent(applicationContext, DefaultTimerLiveDataService::class.java)
//        return Intent(applicationContext, DefaultTimerRxService::class.java)
    }

    /**
     * Handle connection to the TimerService
     */
    private val timerServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            timerServiceBound = false
            timerRxService = null
            timerLiveDataService = null
        }

        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            (binder as? DefaultTimerRxService.TimerRxBinder)?.let { timerServiceBinder ->
                timerRxService = timerServiceBinder.getService()
            }

            (binder as? DefaultTimerLiveDataService.TimerLiveDataBinder)?.let { timerServiceBinder ->
                timerLiveDataService = timerServiceBinder.getService()
            }
            timerServiceBound = true
        }
    }

}

