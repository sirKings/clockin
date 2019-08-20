package com.agromall.clockin.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.agromall.clockin.data.dto.*
import com.agromall.clockin.data.repo.Repository
import com.agromall.clockin.util.AppSchedulers
import com.agromall.clockin.util.EventWrapper
import com.agromall.clockin.util.TimeUtil
import com.google.android.gms.common.internal.ConnectionErrorMessages.getErrorMessage
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.URL

class MainViewModel(
    app: Application,
    private val schedulers: AppSchedulers,
    private val repo: Repository
): AndroidViewModel(app) {

    private val compositeDisposable = CompositeDisposable()
    private val isLoading: MutableLiveData<EventWrapper<Boolean>> = MutableLiveData()
    private val snackBarMessage: MutableLiveData<EventWrapper<String>> = MutableLiveData()

    val staffsRes: MutableLiveData<MutableList<StaffRes>> = MutableLiveData()

    fun loadStaff(id: String) : LiveData<Staff> {
        return repo.getStaff(id)
    }

    fun getAllStaffs() = repo.getAllStaffs()

    fun getAllFP() = repo.getAllFP()

    fun saveAttendance(attendance: Attendance){
        postAttendance(attendance)
        return repo.saveAttendance(attendance)
    }

    fun getAttendance(date: Long, staffId: String): LiveData<Attendance> {
        return repo.getAttendance(date,staffId)
    }

    fun postAttendance(attendance: Attendance){
        val att = AttendancePost(attendance.staffId!!.toInt(), TimeUtil().getTimeForServer(attendance.timeIn!!), null, null)
        compositeDisposable.add(
            repo.postAttendance(att)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.main())
                .subscribe({
                    Log.e("Status", it.result.id.toString())
                    if(!it.status){
                        val id = it.result.id
                        updateAttendanceServer(id, att.staff_id)
                    }
                }, {

                    Log.e("PatT", it.localizedMessage)
                })

        )

    }


    fun updateAttendanceServer(atId: Int, stId: Int){
        val att = AttendancePost(stId, null, atId, TimeUtil().getTimeForServer(System.currentTimeMillis()))
        compositeDisposable.add(
            repo.updateAttendance(att)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.main())
                .subscribe({
                    Log.e("AUpdte", it.message)
                    if(!it.status){
                       // val id = it.results[0].id
                       // updateAttendanceServer(id, att.staff_id)

                    }
                }, {
                    Log.e("AUpdTe", it.message)
                })

        )
    }

    fun updateAttendance(attendance: Attendance){

        return repo.updateAttendance(attendance)
    }

    fun getFp(id: Int) = repo.getFP(id)

    fun savefingerprintId(id: Int, userId: String){
        val fp = FingerprintsModel(null, id,userId)
        repo.saveFP(fp)
    }

    fun getStaffs(){
        compositeDisposable.add(
            repo.getStaffs()
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.main())
                .doOnSubscribe { isLoading.postValue(EventWrapper(true)) }
                .doOnSuccess { isLoading.postValue(EventWrapper(false)) }
                .doOnError { isLoading.postValue(EventWrapper(false)) }
                .subscribe({
                    if (it.results.isNotEmpty()){
                        staffsRes.postValue(it.results as MutableList<StaffRes>)
                    }

                }, {
                    snackBarMessage.value = EventWrapper(it.message)
                })
        )

    }

    fun saveStaff(staff: Staff): Staff?{
        return repo.saveStaff(staff)
    }

    fun getSnackBarMessage(): LiveData<EventWrapper<String>> = snackBarMessage
    fun getIsLoading(): LiveData<EventWrapper<Boolean>> = isLoading

}