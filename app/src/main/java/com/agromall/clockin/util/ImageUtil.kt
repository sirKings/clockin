package com.agromall.clockin.util

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import androidx.annotation.NonNull
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL


class ImageUtil(private val context: Context) {

    private var directoryName = "images"
    private var fileName = "image.png"
    private var external: Boolean = false

    fun setFileName(fileName: String): ImageUtil {
        this.fileName = fileName
        return this
    }

    fun setExternal(external: Boolean): ImageUtil {
        this.external = external
        return this
    }

    fun setDirectoryName(directoryName: String): ImageUtil {
        this.directoryName = directoryName
        return this
    }

    fun save(bitmapImage: Bitmap): File {
        val file = createFile()
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(file)
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream!!.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return file
        }
    }

    @NonNull
    private fun createFile(): File {
        val directory: File
        if (external) {
            directory = getAlbumStorageDir(directoryName)
        } else {
            directory = context.getDir(directoryName, Context.MODE_PRIVATE)
        }
        if (!directory.exists() && !directory.mkdirs()) {
            Log.e("ImageSaver", "Error creating directory $directory")
        }

        return File(directory, fileName)
    }



    private fun getAlbumStorageDir(albumName: String): File {
        return File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), albumName
        )
    }

    fun load(): Bitmap? {
        var inputStream: FileInputStream? = null
        try {
            inputStream = FileInputStream(createFile())
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (inputStream != null) {
                    inputStream!!.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return null
    }

    companion object {

        val isExternalStorageWritable: Boolean
            get() {
                val state = Environment.getExternalStorageState()
                return Environment.MEDIA_MOUNTED.equals(state)
            }

        val isExternalStorageReadable: Boolean
            get() {
                val state = Environment.getExternalStorageState()
                return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)
            }
    }
}
