package com.agromall.clockin.data.source.remote

import com.agromall.clockin.data.dto.*
import com.agromall.clockin.data.source.DataSource
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class RemoteDataSource(val apiService: ApiService) : DataSource {
    override fun postAttendance(attendancePost: AttendancePost): Single<SingleResponse<AttPostRes>> {
        return apiService.postAttendance(attendancePost)
    }

    override fun updateAttendance(attendancePost: AttendancePost): Single<SingleResponse<AttPostRes>> {
        return apiService.updateAttendance(attendancePost)
    }


    override fun getStaffs(): Single<ListResponse<StaffRes>> = apiService.getStaffs()

    override fun postStaff(
        image: MultipartBody.Part,
        fpl: MultipartBody.Part,
        lName: RequestBody,
        fName: RequestBody,
        email: RequestBody,
        dept: RequestBody,
        id: RequestBody?
    ): Single<SingleResponse<String>> = apiService.postStaff(image,fpl,lName,fName,email, dept, id)

    override fun login(request: LoginRequest): Single<ListResponse<String>> = apiService.login(request)

    override fun getDepartment(): Single<ListResponse<String>> = apiService.getDepatments()

    override fun searchReposByName(search: String, page: Int) = apiService.searchRepos(page, search)



}