package com.agromall.clockin.util

import android.content.Context
import com.agromall.clockin.data.dto.Attendance
import com.agromall.clockin.data.dto.AttendancePost
import com.agromall.clockin.data.dto.AttendancePostObject
import com.agromall.clockin.data.source.local.AppDatabase
import com.agromall.clockin.data.source.remote.ApiService
import com.agromall.clockin.di.baseUrl
import com.agromall.clockin.di.httpLoggingInterceptor
import com.agromall.clockin.di.provideHeaderInterceptor
import com.agromall.clockin.di.provideOkhttpClient
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import org.jetbrains.anko.doAsync
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SyncClass(private val context: Context) {

    val db = AppDatabase.getInstance(context)

    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(provideOkhttpClient(provideHeaderInterceptor(),httpLoggingInterceptor()))
        .build()

    val service = retrofit.create(ApiService::class.java)

    fun syncdata(){

        doAsync {

            val attendance = db.getDao().getAllPendingAttendance()

            if(attendance!!.isEmpty()){
                NotificationUtils(context)
                    .createNotification(
                        "Attendance is Upto date",
                        "Attendance is Upto date",
                        (0..10000).random(),
                        false)
            }else{
                uploadAttendance(service, attendance)
            }
        }

    }




    fun uploadAttendance(service: ApiService, inputs: List<Attendance>){

        val id = (0..10000).random()
        val obj = AttendancePostObject()

        inputs.forEach {
            val att = AttendancePost(it.staffId!!.toInt(), TimeUtil().getTimeForServer(it.timeIn)!!, TimeUtil().getTimeForServer(it.timeOut))
            obj.body.add(att)

        }
        NotificationUtils(context)
            .createNotification(
                "Syncing Attendance",
                "Uploading Attendance",
                id,
                true)

        val call = service.postAttendance(obj)
            .subscribe({
                NotificationUtils(context)
                    .createNotification(
                        "Uploading Attendance completed",
                        "Uploading Attendance completed",
                        id,
                        false)
                if(it.status){
                    updateAttendance(obj)
                }
            }, {
                NotificationUtils(context)
                    .createNotification(
                        "Uploading Attendance failed",
                        "Uploading Attendance failed",
                        id,
                        false)

            })

    }


    fun updateAttendance(at: AttendancePostObject){
        at.body.forEach {
            db.getDao().updateAttendance(TimeUtil().getDateFromTimeString(it.time_in), it.staff_id.toString())
        }
    }

}

class CleanupTable(private val context: Context){

    val db = AppDatabase.getInstance(context)

    fun deletePostedAttendance(){
        val items = db.getDao().getPostedAttendance()
        items?.forEach {
            db.getDao().deleteAttendance(it.id!!)
        }

    }

}
