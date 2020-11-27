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

    fun getStaff(id: String) = local.getDao().getStaff(id)

    fun saveStaff(staff: Staff): Staff?{
        local.getDao().insert(staff)
        return local.getDao().getStaff(staff.id)
    }

    fun login(request: LoginRequest) = dataSource.login(request)

    fun getAllStaffs() = local.getDao().getAllStaff()

    fun getAllAttendance() = local.getDao().getAllAttendance()

    fun clearAtt() = local.getDao().clearAtt()

    fun saveAttendance(attendance: Attendance) = local.getDao().saveAttendance(attendance)

    fun getAttendance(date: Long, staffId: String) = local.getDao().getAttendance(date, staffId)

    fun updateAttendance(attendance: Attendance) = local.getDao().updateAttendance(attendance.date!!, attendance.staffId!!)

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

    fun getStaffs(offset: Int) = dataSource.getStaffs(offset)
    fun postAttendance(at: AttendancePostObject) = dataSource.postAttendance(at)

    fun updateAttendance(at: AttendancePost) = dataSource.updateAttendance(at)

    fun getAllFP() = local.getDao().getAllFP()

    fun getPendingAtt() = local.getDao().getAllPendingAttendance()


}