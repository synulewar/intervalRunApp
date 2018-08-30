package com.synowkrz.justynkarun

data class ClockTime(val hours : Long, val minutes: Long, val seconds: Long) {
    companion object {
        val miliInHours = 3600000
        val miliInMinutes = 60000
        val miliInSeconds = 1000

        fun clockFromMilis(miliseconds : Long) : ClockTime {
            var hours = miliseconds / miliInHours
            var minutes = miliseconds % miliInHours / miliInMinutes
            var seconds = miliseconds % miliInHours % miliInMinutes / miliInSeconds
            return ClockTime(hours, minutes, seconds)
        }

        fun clockFromString(time: String) : ClockTime {
            var timeArray = time.split(":")
            if (timeArray.size > 2) {
                return ClockTime(timeArray[0].toLong(), timeArray[1].toLong(), timeArray[2].toLong())
            } else {
                return ClockTime(0, timeArray[0].toLong(), timeArray[1].toLong())
            }
        }

        fun clockToMilisecond(clockTime: ClockTime) : Long {
            return clockTime.hours * miliInHours + clockTime.minutes * miliInMinutes + clockTime.seconds * miliInSeconds
        }

        fun stringToMilis(time: String) : Long {
            var clock = clockFromString(time)
            return ClockTime.clockToMilisecond(clock)
        }

        fun milisFromStringData(runTime: String, walkTime: String, number: String) : Long {
            var runMiliseconds : Long = stringToMilis(runTime)
            var walkMiliseconds : Long = stringToMilis(walkTime)
            var cycles = number.toLong()
            return (runMiliseconds + walkMiliseconds) * cycles
        }
    }

}