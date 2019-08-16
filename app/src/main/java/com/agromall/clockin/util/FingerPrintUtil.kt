package com.agromall.clockin.util

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import cn.com.aratek.fp.FingerprintImage
import com.agromall.clockin.data.source.local.FingerprintDB
import com.innovatrics.idkit.IDKit
import com.innovatrics.idkit.IDKitException
import com.innovatrics.idkit.User
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class FingerprintUtil(private val mContext: Context) {
    private val LICENSE_FILE = "res/raw/iengine.lic"
    private var idkit: IDKit? = null


    fun create(): FingerprintUtil {
        //Log.e("This device's HWID is: ", String(IDKit.getHardwareId()))
        //Log.e("IDKTAG Build.SERIAL", Build.SERIAL)
        val db = FingerprintDB(mContext)
        try {
            val license = readFileResource(LICENSE_FILE)
            //initialize matching SDK
            IDKit.initWithLicense(license)
            //instantiate the Matching SDK
            idkit = IDKit.getInstance()

            idkit?.connect(mContext.getDatabasePath(FingerprintDB.DB_NAME).toString())
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return this
    }

    fun registerUser(fpImage: FingerprintImage): Int{
        return registerFP(fpImage)
    }

    fun registerUserByte(img: ByteArray): Int{
        var user: User? = null
        var id = -1
        Log.e("image size:", img.size.toString())
        try {
            user = User(idkit)

            //idkit?.setParameter(IDKit.ConfigParameter.CFG_RESOLUTION_DPI, 500)
            user.addFingerprint(IDKit.FingerPosition.UNKNOWN_FINGER, img)

        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("IDKIT IO EXCP ExtractFP", e.message)
        } catch (e: IDKitException) {
            e.printStackTrace()
            Log.e("IDKIT EXPT ExtractFP", e.message)
        }

        try {
            idkit?.registerUser(user!!)
            //find the newly registered user
            val result = idkit?.findUser(user)
            if (!result.isNullOrEmpty()) {
                //update user
                id = result[0].userID
            }
        } catch (e: IDKitException) {
            Log.e("IDKIT EXCP regFP", e.message)
        }

        Log.e("Saved user id", id.toString())

        return id

    }

    @Throws(IOException::class)
    private fun readFileResource(resourcePath: String): ByteArray {
        val `is` = this.javaClass.classLoader?.getResourceAsStream(resourcePath)
            ?: throw IOException("cannot find resource: $resourcePath")
        return getBytesFromInputStream(`is`)
    }

    @Throws(IOException::class)
    private fun getBytesFromInputStream(`is`: InputStream): ByteArray {
        val os = ByteArrayOutputStream()
        val buffer = ByteArray(4096)
        var len: Int = `is`.read(buffer)
        while (len != -1) {
            os.write(buffer, 0, len)
            len = `is`.read(buffer)
        }
        os.flush()
        return os.toByteArray()
    }

    private fun extractFingerPrint(image: FingerprintImage): User? {
        var user: User? = null
        try {
            user = User(idkit)

            //idkit?.setParameter(IDKit.ConfigParameter.CFG_RESOLUTION_DPI, 500)
            user.addFingerprint(IDKit.FingerPosition.UNKNOWN_FINGER, image.convert2Bmp())

        } catch (e: IOException) {
            e.printStackTrace()
            Log.v("IDKIT IO EXCP ExtractFP", e.message)
        } catch (e: IDKitException) {
            e.printStackTrace()
            Log.v("IDKIT EXPT ExtractFP", e.message)
        }

        return user
    }

    private fun registerFP(image: FingerprintImage): Int {
        try {
            val user = extractFingerPrint(image)
            idkit?.registerUser(user!!)
            //find the newly registered user
            val result = idkit?.findUser(user)
            if (!result.isNullOrEmpty()) {
                //update user
                return result[0].userID
            }
        } catch (e: IDKitException) {
            Log.v("IDKIT EXCP regFP", e.message)
        }
        return -1
    }

    fun checkDuplicatePrint(fpImage: FingerprintImage): Boolean{
        try {
            val user = extractFingerPrint(fpImage)
            //find the newly registered user
            val result = idkit?.findUser(user)
            if (!result.isNullOrEmpty()) {
                //update user
                Log.e("user exists", result[0].userID.toString())
                return true
            }
        } catch (e: IDKitException) {
            Log.v("IDKIT EXCP regFP", e.message)
        }
        return false
    }

    fun clearFingerDB(){
        idkit?.clearDatabase()
    }

    fun getUserId(fpImage: FingerprintImage): Int{
        try {
            val user = extractFingerPrint(fpImage)
            //find the newly registered user
            val result = idkit?.findUser(user)
            if (!result.isNullOrEmpty()) {
                //update user
                Log.e("user exists", result[0].userID.toString())
                return result[0].userID
            }
        } catch (e: IDKitException) {
            Log.v("IDKIT EXCP regFP", e.message)
        }

        return -1
    }
}

