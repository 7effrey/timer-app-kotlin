package com.jeffrey.timer.service

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import org.junit.After


class DefaultTimerRxViewModelTest {

    private lateinit var viewModel: DefaultTimerRxViewModel
    private lateinit var viewData: TimerRxViewModel.ViewData

    private var disposable: Disposable? = null

    private val veStart = PublishSubject.create<Long>()
    private val vePause = PublishSubject.create<Unit>()
    private val veStop = PublishSubject.create<Unit>()

    private val scheduler = TestScheduler()

    @Before
    fun setup() {
        // Set calls to Schedulers.computation() that is used by Observable.interval() to use test scheduler
        RxJavaPlugins.setComputationSchedulerHandler { scheduler }

        viewModel = DefaultTimerRxViewModel(scheduler, scheduler)
        disposable = viewModel.set(object: TimerRxViewModel.ViewEvent {
            override val start: Observable<Long> = veStart
            override val pause: Observable<Unit> = vePause
            override val stop: Observable<Unit> = veStop
        })
        viewData = viewModel.generateViewData()
    }

    @After
    fun tearDown() {
        RxJavaPlugins.setComputationSchedulerHandler(null)
        disposable?.dispose()
    }

    @Test
    fun `viewEvent pause() should update runningStatus viewData value to false`() {
        val obs = viewData.runningStatus.test()
        vePause.onNext(Unit)
        obs.assertValues(false, false)
    }

    @Test
    fun `viewEvent pause() should not update remainingTime and remainingSeconds viewData value to 0`() {
        val obsTime = viewData.remainingTime.test()
        val obsSeconds = viewData.remainingSeconds.test()
        vePause.onNext(Unit)
        obsSeconds.assertValues(0)
        obsTime.assertValues("00:00")
    }

    @Test
    fun `viewEvent stop() should update runningStatus viewData value to false`() {
        val obs = viewData.runningStatus.test()
        veStop.onNext(Unit)
        obs.assertValues(false, false)
    }

    @Test
    fun `viewEvent stop() should update remainingTime and remainingSeconds viewData value to 0`() {
        val obsTime = viewData.remainingTime.test()
        val obsSeconds = viewData.remainingSeconds.test()
        veStop.onNext(Unit)
        obsSeconds.assertValues(0, 0)
        obsTime.assertValues("00:00", "00:00")
    }

    @Test
    fun `viewEvent start() should update runningStatus viewData value to true`() {
        val obs = viewData.runningStatus.test()
        veStart.onNext(5)
        obs.assertValues(false, true)
    }

    @Test
    fun `viewEvent start() should update remainingSeconds viewData value with countdown seconds`() {
        val obs = viewData.remainingSeconds.test()
        veStart.onNext(5)
        scheduler.advanceTimeBy(10, TimeUnit.SECONDS)
        obs.assertValues(0, 5, 4, 3, 2, 1, 0)

        val obs2 = viewData.remainingSeconds.test()
        veStart.onNext(10)
        scheduler.advanceTimeBy(4, TimeUnit.SECONDS)
        obs2.assertValues(0, 10, 9, 8, 7, 6)
    }

    @Test
    fun `viewEvent start() should update remainingTime viewData value with countdown seconds`() {
        val obs = viewData.remainingTime.test()
        veStart.onNext(3)
        scheduler.advanceTimeBy(5, TimeUnit.SECONDS)
        obs.assertValues("00:00", "00:03", "00:02", "00:01", "00:00")

        val obs2 = viewData.remainingTime.test()
        veStart.onNext(10)
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)
        obs2.assertValues("00:00", "00:10", "00:09", "00:08")
    }
}