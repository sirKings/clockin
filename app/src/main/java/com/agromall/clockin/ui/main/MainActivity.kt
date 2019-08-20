package com.agromall.clockin.ui.main
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import cn.com.aratek.fp.Bione
import cn.com.aratek.fp.FingerprintImage
import cn.com.aratek.fp.FingerprintScanner
import cn.com.aratek.util.Result
import com.agromall.clockin.data.dto.Attendance
import com.agromall.clockin.data.dto.Staff
import com.agromall.clockin.util.FingerprintUtil
import com.agromall.clockin.util.TimeUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.agromall.clockin.R
import com.google.android.material.snackbar.Snackbar
import com.agromall.clockin.data.dto.StaffRes
import com.agromall.clockin.util.ImageUtil
import kotlinx.android.synthetic.main.clockin_layout.view.*
import org.jetbrains.anko.onComplete
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.net.URL


class MainActivity : AppCompatActivity() {

    private val FP_DB_PATH = "/sdcard/fp.db"


    private var fingerPrint: FingerprintImage? = null
    private var scanner: FingerprintScanner? = null
    private var fpImageBmp: Bitmap? = null

    var isUpdate = false
    private val vModel by viewModel<MainViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        heading.visibility = View.GONE

        retryBtn.visibility = View.GONE
        progress.visibility = View.GONE

        timeInfo.visibility = View.GONE

        retryBtn.setOnClickListener {
            setUpScanner()
            //verrifyPrint(2)
        }

        bcakBtn.setOnClickListener {
            //finish()
        }

        bcakBtn.visibility = View.GONE

        vModel.staffsRes.observe(this, Observer {
            val staffList = ArrayList<StaffRes>()
            val latestId = getSharedPreferences("agromall.clockin", 0).getInt("latestId", 0)
            it.forEach {
                progress.visibility = View.VISIBLE
                Log.e("items", it.first_name)
                if(it.id < latestId){

                }else{
                    staffList.add(it)
                }
                //saveStaffLocally(it)
            }

            Log.e("latestId res", latestId.toString())

            saveStaffLocally(staffList)
        })

        vModel.getIsLoading().observe(this, Observer {
            progress?.visibility =
                if(it?.getContentIfNotHandled() != null && it.peekContent() == true)
                    View.VISIBLE
                else
                    View.INVISIBLE

        })

        vModel.getSnackBarMessage().observe(this, Observer {
            if (it?.getContentIfNotHandled() != null){
                Snackbar.make(myView, "${it.peekContent()}", Snackbar.LENGTH_LONG).show()
            }
        })

        vModel.getAllStaffs().observe(this, Observer {
            it.forEach {
                Log.e("staff", it.staffId)
                Log.e("staff", it.lastName)
                Log.e("staff", it.image)
                Log.e("staff", it.firstName)
                Log.e("staff", it.id)
            }
        })

        vModel.getAllFP().observe(this, Observer {
            it.forEach {
                Log.e("fp userid", it.userId)
                Log.e("fp fpid", it.fpId.toString())
                Log.e("fp id", it.id.toString())
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(Menu.NONE, 1, Menu.NONE, "Sync")
        menu.add(Menu.NONE, 2, Menu.NONE, "Close")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            1 -> {
                loadUsers()
                return true
            }
            2 -> {
                finish()
                return true
            }
            else -> return false
        }
    }

    fun loadUsers(){
        vModel.getStaffs()
    }

    override fun onStart() {
        super.onStart()
        setUpScanner()
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT)
    }

    private fun setUpScanner(){
        infoText.text = "Place your finger on the scanner"
        doAsync {
            scanner = FingerprintScanner.getInstance(this@MainActivity)
            scanner?.powerOn()
            scanner?.open()
            scanner?.prepare()
            Bione.initialize(this@MainActivity, FP_DB_PATH)
            var res: Result? = null
            do {
                res = scanner?.capture()
            } while (res?.error == FingerprintScanner.NO_FINGER)
            scanner?.finish()

            if (checkFingerPrint(res?.data as FingerprintImage)) {
                uiThread {
                    infoText.text = "Verifying fingerprint"
                    infoText.setTextColor(resources.getColor(android.R.color.black))
                    fingerPrint = res.data as FingerprintImage
                    val fPByteArray = fingerPrint?.convert2Bmp()
                    fpImageBmp = BitmapFactory.decodeByteArray(fPByteArray, 0, fPByteArray!!.size)
                    imageView8.setImageBitmap(fpImageBmp)
                    retryBtn.visibility = View.GONE
                    if(!isUpdate){
                        verrifyPrint(fingerPrint!!)
                    }
                }
            } else {
                uiThread {
                    retryBtn.visibility = View.VISIBLE
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        scanner?.close()
        scanner?.powerOff()
        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onBackPressed() {
        doAsync {
            scanner?.close()
            scanner?.powerOff()
            //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        scanner = FingerprintScanner.getInstance(this@MainActivity)
        scanner?.powerOn()
        scanner?.open()
        scanner?.prepare()
        Bione.initialize(this@MainActivity, FP_DB_PATH)
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT)
    }

    fun checkFingerPrint(result: FingerprintImage): Boolean{

        val res = Bione.extractFeature(result)

        if (res.error != Bione.RESULT_OK){
            runOnUiThread {
                Log.e("err", "Extraction failed")
                infoText?.text = "Place your finger well on the scanner"
                infoText?.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                val fPByteArray = result.convert2Bmp()
                val fpI = BitmapFactory.decodeByteArray(fPByteArray, 0, fPByteArray!!.size)
                imageView8.setImageBitmap(fpI)
            }
            return false
        }

        return true
    }

    private fun verrifyPrint(fi: FingerprintImage){

//        //
        doAsync {
            val userId = FingerprintUtil(this@MainActivity)
                .create()
                .getUserId(fi)

            Log.e("id", userId.toString())
            uiThread {
                if(userId != -1){

                    vModel.getFp(userId).observe(this@MainActivity, Observer {
                        Log.e("Watch", "Getting user fp")
                        if(it != null){
                            Log.e("Watch", "Found user fp${it.fpId}")
                            vModel.loadStaff(it.userId!!).observe(this@MainActivity, Observer {
                                if (it == null){
                                    infoText.text = "Staff not found"
                                    retryBtn.visibility = View.VISIBLE
                                    imageView8.setImageDrawable(resources.getDrawable(R.drawable.ic_fingerprint_gray))
                                }else{
                                    if(!isUpdate){
                                        setupUser(it)
                                    }
                                    retryBtn.visibility = View.GONE
                                }
                            })
                        }else{
                            infoText.text = "Staff not found"
                            retryBtn.visibility = View.VISIBLE
                            imageView8.setImageDrawable(resources.getDrawable(R.drawable.ic_fingerprint_gray))
                        }
                    })
                }else{
                    infoText.text = "This staff does not exist"
                    retryBtn.visibility = View.VISIBLE
                    imageView8.setImageDrawable(resources.getDrawable(R.drawable.ic_fingerprint_gray))
                }
            }
        }

    }

    private fun setupUser(staff: Staff){

        user_profile_name.text = "${staff.firstName}  ${staff.lastName}"
        user_profile_dept.text = "${staff.department}"
        imageView8.setImageDrawable(resources.getDrawable(R.drawable.ic_fingerpirnt_green))

        saveAttendance(staff.id, staff)

    }

    fun saveAttendance(id: String, staff: Staff){
        vModel.getAttendance(TimeUtil().getDateInMilliseconds(), id).observe(this, Observer {

            if(!isUpdate){
                if(it ==  null){
                    val att = Attendance(null, id, System.currentTimeMillis(), null, TimeUtil().getDateInMilliseconds())
                    doAsync {

                        vModel.saveAttendance(att)
                        //vModel.postAttendance(att)
                        isUpdate = true
                        uiThread {
                            showInfo("Welcome ${staff.firstName}","Time in: ${TimeUtil().getTimeinString(att.timeIn!!)}", staff.staffId)
                        }
                    }
                }else if ( it.timeOut == null){
                    it.timeOut = System.currentTimeMillis()


                    doAsync {
                        vModel.saveAttendance(it)
                        //vModel.postAttendance(it)
                        isUpdate = true
                        val at = it
                        uiThread {
                            showInfo( "Goodbye ${staff.firstName}","Time out: ${TimeUtil().getTimeinString(at.timeOut!!)}. Spent ${TimeUtil().getTimeDif(at.timeIn!!, at.timeOut!!)}", staff.staffId)
                        }
                    }
                }else{
                    isUpdate = true
                    showInfo(  "You have clocked out already today","Time out: ${TimeUtil().getTimeinString(it.timeOut!!)}. Spent ${TimeUtil().getTimeDif(it.timeIn!!, it.timeOut!!)}", staff.staffId)
                }
            }
        })
    }

    fun showInfo(greeting: String, info: String, imgUrl: String){

        val builder = AlertDialog.Builder(this)
        val customLayout = layoutInflater.inflate(R.layout.clockin_layout, null)
        builder.setView(customLayout)
        val dialog = builder.create()

        customLayout.greeting.text = greeting
        customLayout.info.text = info
        //Picasso.with(this).load(imgUrl).into(customLayout.imageView5)
        customLayout.imageView5.setImageBitmap(ImageUtil(this).setDirectoryName("images").setFileName(imgUrl).load())

        customLayout.closeBtn.setOnClickListener {
            dialog.dismiss()
            infoText.text = "Place your finger on the scanner"
            //user_image.setImageDrawable(null)
            user_profile_name.text = ""
            user_profile_dept.text = ""
            //retryBtn.visibility = View.VISIBLE
            isUpdate = false
            imageView8.setImageDrawable(resources.getDrawable(R.drawable.ic_fingerprint_gray))
            setUpScanner()
        }

        dialog.setCancelable(false)
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.transparent)))

    }

    fun updateAttendance(id: String){
        var isUpdate = false
        vModel.getAttendance(TimeUtil().getDateInMilliseconds(), id).observe(this, Observer {
            if(!isUpdate){
                if(it != null){
                    if(it.timeOut == null){
                        it.timeOut = System.currentTimeMillis()
                        doAsync {
                            vModel.saveAttendance(it)
                            isUpdate = true
                            val at = it
                            uiThread {
                                timeInfo.text = "Time out: ${TimeUtil().getTimeinString(at.timeOut!!)}. Spent ${TimeUtil().getTimeDif(at.timeIn!!, at.timeOut!!)}"
                                timeInfo.visibility = View.VISIBLE
                            }
                        }
                    }else{
                        infoText.text = "You have clocked out already today"
                        timeInfo.text = "Time out: ${TimeUtil().getTimeinString(it.timeOut!!)}. Spent ${TimeUtil().getTimeDif(it.timeIn!!, it.timeOut!!)}"
                        timeInfo.visibility = View.VISIBLE
                    }
                }else{
                    infoText.text = "This staff has not clocked in today. Please clock in first"
                }
            }
        })
    }

    private fun saveStaffLocally(st: ArrayList<StaffRes>){
        var latestId = 0
        doAsync {
            st.forEach {
                val staff = ResToStaff(it)
                if (latestId < it.id){
                    latestId = it.id
                }
                downloadImage(staff.fingerPrint, staff.staffId, true)
                downloadImage(staff.fingerPrint1, staff.staffId, true)
                downloadImage(staff.image, staff.staffId, false)
                vModel.saveStaff(staff)
            }
            onComplete {
                getSharedPreferences("agromall.clockin", 0).edit().putInt("latestId",latestId).apply()
                Log.e("latestId in", latestId.toString())
                Snackbar.make(myView, "Sync completed", Snackbar.LENGTH_LONG).show()
                progress.visibility = View.GONE
            }
        }

    }

    private  fun ResToStaff(st: StaffRes): Staff{
        val base_path ="https://s3-eu-west-1.amazonaws.com/agromall-storage/"
         return Staff(st.id.toString(), st.staff_id, st.first_name, st.last_name, st.department, "$base_path${st.image_path}",st.status,"", "", "$base_path${st.right_finger_print_path}", "$base_path${st.left_finger_print_path}")
    }



    fun downloadImage(str: String, uid: String, isFP: Boolean){
        val url = URL(str)
        val `in` = BufferedInputStream(url.openStream())
        val out = ByteArrayOutputStream()
        val buf = ByteArray(1024)
        var n = 0
        while (-1 != n) {
            out.write(buf, 0, n)
            n = `in`.read(buf)
        }
        out.close()
        `in`.close()
        val response = out.toByteArray()
        Log.e("downloaded", response.size.toString())
        if(isFP){
            val id = FingerprintUtil(this).create().registerUserByte(response)
            if(id != -1){
                vModel.savefingerprintId(id,uid)
            }
        }else{
            val btmp = BitmapFactory.decodeByteArray(response, 0, response.size)
            ImageUtil(this).setDirectoryName("images").setFileName(uid).save(btmp)
        }

    }


}


