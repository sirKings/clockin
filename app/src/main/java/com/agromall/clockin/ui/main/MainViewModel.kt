package com.agromall.clockin.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.agromall.clockin.data.dto.*
import com.agromall.clockin.data.repo.Repository
import com.agromall.clockin.util.AppSchedulers
import com.agromall.clockin.util.EventWrapper
import io.reactivex.disposables.CompositeDisposable

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
        return repo.saveAttendance(attendance)
    }

    fun getAttendance(date: Long, staffId: String): LiveData<Attendance> {
        return repo.getAttendance(date,staffId)
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