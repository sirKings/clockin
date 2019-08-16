package com.agromall.clockin.util

import java.text.SimpleDateFormat
import java.util.*



class TimeUtil {

    fun getDateInMilliseconds(): Long{
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        val date = formatter.format(System.currentTimeMillis())
        var dateObject = Date()

        try {
            dateObject = formatter.parse(date)
        }catch (err: Exception){
            err.printStackTrace()
        }
        return dateObject.time

    }

    fun getTimeinString(time: Long): String{
        val formatter = SimpleDateFormat("hh:mm aa")
        return formatter.format(time)
    }

    fun getTimeDif(timeIn: Long, timeOut: Long): String{
        val difference = timeOut - timeIn

        val days = (difference / (1000 * 60 * 60 * 24)).toInt()
        val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt()
        val min = (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours).toInt() / (1000 * 60)

        if (hours == 0 && min != 0){
            return "$min minute(s) in the office today"
        }else if (min == 0 && hours != 0){
            return "$hours hour(s) in the office today"
        }

        return "$hours hours, $min minutes in the office today"
    }
}