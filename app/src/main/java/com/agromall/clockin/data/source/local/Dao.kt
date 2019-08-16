package com.agromall.clockin.data.source.local


import androidx.lifecycle.LiveData
import androidx.room.*
import com.agromall.clockin.data.dto.Attendance
import com.agromall.clockin.data.dto.FingerprintsModel
import com.agromall.clockin.data.dto.Staff
import io.reactivex.Single

@Dao
interface StaffDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(staff: Staff)

    @Query("DELETE FROM staffs")
    fun deleteAllStaff()

    @Query("SELECT * FROM staffs ")
    fun getAllStaff(): LiveData<List<Staff>>

    @Query("SELECT * FROM fingerDB")
    fun getAllFP(): LiveData<List<FingerprintsModel>>

    @Query("SELECT * FROM staffs WHERE staffId = :id")
    fun getStaff(id: String): LiveData<Staff>

    @Query("SELECT * FROM attendance WHERE date = :date AND staffId = :staffId")
    fun getAttendance(date: Long, staffId: String): LiveData<Attendance>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAttendance(attendance: Attendance)

    @Update
    fun updateAttendance(attendance: Attendance)

    @Query("SELECT * FROM attendance")
    fun getAllAttendance(): LiveData<List<Attendance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savefingerPrint(fp: FingerprintsModel)

    @Query("SELECT * FROM fingerDB WHERE fpId = :id")
    fun getFPInfo(id: Int): LiveData<FingerprintsModel>

}