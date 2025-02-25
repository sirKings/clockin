package com.agromall.clockin.util

import android.util.Log
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
        val d = dateObject.time

        Log.e("date", d.toString())

        return d
    }

    fun getDateFromTimeString(time: String): Long{
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.UK)
        val timeformat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK)
        var dateObject = Date()
        try {
            val date = formatter.format(timeformat.parse(time))
            dateObject = formatter.parse(date)
        }catch (err: Exception){
            err.printStackTrace()
        }
        val d = dateObject.time

        Log.e("date", d.toString())

        return d
    }

    fun getTimeinString(time: Long): String{
        val formatter = SimpleDateFormat("hh:mm aa")
        return formatter.format(time)
    }

    fun getTimeForServer(time: Long?): String?{
        if(time == null){
            return null
        }
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK)
        return format.format(time)
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