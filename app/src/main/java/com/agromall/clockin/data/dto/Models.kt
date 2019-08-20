package com.agromall.clockin.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "staffs")
data class Staff (
    @PrimaryKey
    var id: String,
    var staffId: String,
    var firstName: String,
    var lastName: String,
    var department: String,
    var image: String,
    var status: String,
    var createdAt: String,
    var updatedAt: String,
    var fingerPrint: String,
    var fingerPrint1: String
)

@Entity(tableName = "attendance")
data class Attendance (
    @PrimaryKey(autoGenerate = true)
    var id: Long?,
    var staffId: String?,
    var timeIn: Long?,
    var timeOut: Long?,
    var date: Long?
)

data class LoginRequest(
    var email: String,
    var password: String
)

@Entity(tableName = "fingerDB")
data class FingerprintsModel(
    @PrimaryKey(autoGenerate = true)
    var id: Long?,
    var fpId: Int?,
    var userId: String?
)

data class StaffRes(
    var id: Int,
    var first_name: String,
    var last_name: String,
    var staff_id: String,
    var email: String,
    var department: String,
    var image_path: String,
    var status: String,
    var right_finger_print_path: String,
    var left_finger_print_path: String,
    var time_in: String?,
    var time_out: String?
)

data class AttendancePost(
    var staff_id: Int,
    var time_in: String?,
    var attendance_id: Int?,
    var time_out: String?
)

data class AttPostRes(
    var id: Int
)

data class AttResponse (
    val message: String,
    val status: Boolean,
    val data: AttPostRes
)


