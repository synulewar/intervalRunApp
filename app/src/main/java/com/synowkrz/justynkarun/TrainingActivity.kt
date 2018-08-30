package com.synowkrz.justynkarun

import android.os.Bundle
import android.os.SystemClock
import android.speech.tts.TextToSpeech
import android.support.v7.app.AppCompatActivity
import android.transition.Visibility
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_training.*
import java.util.concurrent.TimeUnit

class TrainingActivity: AppCompatActivity() {

    val TAG = "KRZYS"
    val SECOND = 1000

    var timerRX : Observable<Long> = Observable.interval(1, TimeUnit.SECONDS)
    var currentType: IntervalType = IntervalType.RUN
    lateinit var disposableTimer : Disposable
    lateinit var textToSpeech: TextToSpeech
    var speechReady = false
    var initialRunMilis : Long = 0
    var initialWalkMilis: Long = 0
    var currentIntervalTime : Long = 0
    var currentTotalTime: Long = 0
    var cyclesLeft : Int = 0
    var trainingInProgress = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training)
        var runTime = intent.extras.getString(ContractValues.RUN)
        var walkTime = intent.extras.getString(ContractValues.WALK)
        var number = intent.extras.getString(ContractValues.NUMBER)
        currentIntervalLabel.text = getString(R.string.runDisplay)
        initialRunMilis = ClockTime.stringToMilis(runTime)
        initialWalkMilis = ClockTime.stringToMilis(walkTime)
        currentTotalTime = ClockTime.milisFromStringData(runTime, walkTime, number)
        cyclesLeft = number.toInt()
        currentType = IntervalType.RUN
        currentIntervalTime = initialRunMilis
        bindTrainingView()
        textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            Log.d(TAG, "TextToSpeech initialized with status " + status)
            if (status != TextToSpeech.ERROR) {
                Log.d(TAG, "Default voice " + textToSpeech.defaultVoice.toString())
            }
        })


    }

    private fun bindTrainingView() {
        setCurrentIntervalTime()
        setTotalTime()
        cycleNumberLef.text = cyclesLeft.toString()
        startStopButton.setOnClickListener {
            if (trainingInProgress) {
                stopTraining()
            } else {
                startTraining()
            }
        }

        finishButton.setOnClickListener {
            finish()
        }
    }

    private fun setCurrentIntervalTime() {
        if (currentIntervalTime > 0) {
            var clockTime = ClockTime.clockFromMilis(currentIntervalTime)
            currentIntervalTimeLeft.text = String.format("%02d:%02d",
                    clockTime.minutes, clockTime.seconds)
        } else {
            startNewInterval()
        }
    }

    private fun setTotalTime() {
        if (currentTotalTime > 0) {
            var clockTime = ClockTime.clockFromMilis(currentTotalTime)
            trainingTimeLeft.text = String.format("%02d:%02d:%02d",
                    clockTime.hours, clockTime.minutes, clockTime.seconds)
        } else {
            endTraining()
        }
    }

    private fun endTraining() {
        trainingTimeLeft.text = String.format("%02d:%02d:%02d",
                0, 0, 0)
        currentIntervalTimeLeft.text = String.format("%02d:%02d",
                0, 0)
        startStopButton.visibility = View.INVISIBLE
        finishButton.visibility = View.VISIBLE
        disposableTimer.dispose()
        textToSpeech.speak(SpeakToMe.END_TRAINING, TextToSpeech.QUEUE_ADD, null, null)

    }

    private fun startNewInterval() {
        Log.d(TAG, "startNewInterval " + currentType.toString())
        if (currentType == IntervalType.WALK) {
            if (cyclesLeft > 1) {
                textToSpeech.speak(SpeakToMe.RUN, TextToSpeech.QUEUE_ADD, null, null)
            }
            currentType = IntervalType.RUN
            currentIntervalLabel.text = getString(R.string.runDisplay)
            currentIntervalTime = initialRunMilis
            if (cyclesLeft > 0) {
                cyclesLeft--
                cycleNumberLef.text = cyclesLeft.toString()
            }

        } else {
            textToSpeech.speak(SpeakToMe.WALK, TextToSpeech.QUEUE_ADD, null, null)
            currentType = IntervalType.WALK
            currentIntervalLabel.text = getString(R.string.walkDisplay)
            currentIntervalTime = initialWalkMilis
        }
        setCurrentIntervalTime()
    }

    private fun startTraining() {

        textToSpeech.speak(SpeakToMe.BEGIN_TRAINING, TextToSpeech.QUEUE_ADD, null, null)
        trainingInProgress = true
        disposableTimer = timerRX
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    currentIntervalTime -= SECOND
                    currentTotalTime -= SECOND
                    Log.d(TAG, "Interval " + currentIntervalTime + " total " + currentTotalTime)
                    setCurrentIntervalTime()
                    setTotalTime()
                }
        startStopButton.text = getString(R.string.stopButtonText)
        finishButton.visibility = View.INVISIBLE
    }

    private fun stopTraining() {
        trainingInProgress = false
        disposableTimer.dispose()
        finishButton.visibility = View.VISIBLE
        startStopButton.text = getString(R.string.startButtonText)
    }

    override fun onBackPressed() {
        Log.d(TAG, "Back button disabled!" )
    }
}