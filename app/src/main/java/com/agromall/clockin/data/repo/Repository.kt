package com.agromall.clockin.data.repo

import androidx.lifecycle.LiveData
import com.agromall.clockin.data.dto.*
import com.agromall.clockin.data.source.DataSource
import com.agromall.clockin.data.source.local.AppDatabase
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository(
    private val dataSource: DataSource,
    private val local: AppDatabase
) {

    fun searchRepository(search: String, page: Int) = dataSource.searchReposByName(search, page)

    fun getDepts() = dataSource.getDepartment()

    fun getStaff(id: String): LiveData<Staff>{
        return local.getDao().getStaff(id)
    }

    fun saveStaff(staff: Staff): Staff?{
        local.getDao().insert(staff)
        return local.getDao().getStaff(staff.id).value
    }

    fun login(request: LoginRequest) = dataSource.login(request)

    fun getAllStaffs() = local.getDao().getAllStaff()

    fun getAllAttendance() = local.getDao().getAllAttendance()

    fun saveAttendance(attendance: Attendance) = local.getDao().saveAttendance(attendance)

    fun getAttendance(date: Long, staffId: String) = local.getDao().getAttendance(date, staffId)

    fun updateAttendance(attendance: Attendance) = local.getDao().updateAttendance(attendance)

    fun getFP(id: Int) = local.getDao().getFPInfo(id)

    fun saveFP(fp: FingerprintsModel) = local.getDao().savefingerPrint(fp)

    fun postStaff(
        image: MultipartBody.Part,
        fpl: MultipartBody.Part,
        lName: RequestBody,
        fName: RequestBody,
        email: RequestBody,
        dept: RequestBody,
        id: RequestBody?
    ) = dataSource.postStaff(image,fpl,lName,fName,email, dept, id)

    fun getStaffs() = dataSource.getStaffs()
    fun postAttendance(at: AttendancePost) = dataSource.postAttendance(at)

    fun updateAttendance(at: AttendancePost) = dataSource.updateAttendance(at)

    fun getAllFP() = local.getDao().getAllFP()


}