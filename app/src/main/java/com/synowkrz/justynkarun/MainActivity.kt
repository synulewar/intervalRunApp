package com.synowkrz.justynkarun

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = "KRZYS"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var timeList: MutableList<String> = mutableListOf()
        var intervalList: MutableList<String> = mutableListOf()
        for (i in  0..60) {
            timeList.add(String.format("%02d", i) + ":00")
            timeList.add(String.format("%02d", i) + ":30")
            if (i > 0) {
                intervalList.add(i.toString())
            }
        }
        runSpinner.adapter = ArrayAdapter<String> (this, R.layout.spinner_element, timeList)
        walkSpinner.adapter = ArrayAdapter<String> (this, R.layout.spinner_element, timeList)
        intervalSpinner.adapter = ArrayAdapter<String> (this, R.layout.spinner_element, intervalList)

        startTraining.setOnClickListener {
            var runTime = runSpinner.selectedItem as String
            var walkTime = walkSpinner.selectedItem as String
            var intervalNumber = intervalSpinner.selectedItem as String
            Log.d(TAG, runTime + " " + walkTime + " " + intervalNumber)
            var intent = Intent(this, TrainingActivity::class.java)
            intent.putExtra(ContractValues.RUN, runTime)
            intent.putExtra(ContractValues.WALK, walkTime)
            intent.putExtra(ContractValues.NUMBER, intervalNumber)
            startActivity(intent)
        }
    }
}
