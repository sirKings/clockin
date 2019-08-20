package com.agromall.clockin.data.source

import com.agromall.clockin.data.dto.*
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Part

interface DataSource {
    fun searchReposByName(search: String, page: Int) : Single<ListResponse<Staff>>
    fun getDepartment(): Single<ListResponse<String>>
    fun login(request: LoginRequest): Single<ListResponse<String>>
    fun postStaff(image: MultipartBody.Part,
                  fpl: MultipartBody.Part,
                  lName: RequestBody,
                  fName: RequestBody,
                  email: RequestBody,
                  dept: RequestBody,
                  id: RequestBody?
    ): Single<SingleResponse<String>>

    fun getStaffs(): Single<ListResponse<StaffRes>>

    fun postAttendance(attendancePost: AttendancePost): Single<SingleResponse<AttPostRes>>

    fun updateAttendance(attendancePost: AttendancePost): Single<SingleResponse<AttPostRes>>
}