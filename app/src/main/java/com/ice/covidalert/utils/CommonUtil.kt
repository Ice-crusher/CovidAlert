package com.ice.covidalert.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.provider.Settings
import android.util.Base64
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import com.ice.covidalert.CovidApplication
import io.reactivex.Single
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CommonUtil {

    companion object {
        @SuppressLint("HardwareIds")
        fun getDeviceId(context: Context): String {
            return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }

        fun loadJSONFromAsset(fileName: String): String? {
            var json: String? = null
            try {
                val f = CovidApplication.instance.assets.open(fileName)
                val size = f.available()
                val buffer = ByteArray(size)
                f.read(buffer)
                f.close()
                json = String(buffer)
            } catch (ex: IOException) {
                ex.printStackTrace()
                return null
            }

            return json
        }

        fun bitmapToBase64(bitmap: Bitmap): Single<String> {
            return Single.create {val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()

                it.onSuccess( Base64.encodeToString(byteArray, Base64.NO_WRAP))
            }

        }
        fun bitmapsToBase64(bitmaps: ArrayList<Bitmap>): Single<ArrayList<String>> {
            return Single.create {
                var result = ArrayList<String>()
                for (bitmap: Bitmap in bitmaps) {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    val byteArray = byteArrayOutputStream.toByteArray()
                    result.add(Base64.encodeToString(byteArray, Base64.NO_WRAP))
                }
                it.onSuccess(result)
            }

        }

        fun dpToPx(dip : Float) : Int {
            var r = CovidApplication.instance.resources
            var px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.displayMetrics
            )
            return Math.round(px)
        }

        fun <T> arrayToArrayList(array: Array<T>): ArrayList<T> {
            var arrayResult = ArrayList<T>()
            for (item in array) {
                arrayResult.add(item)
            }
            return arrayResult
        }

        fun hideKeyboard(context: Context, view: View) {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

        }

        fun openKeyboard(context: Context, view: View) {

            Handler().postDelayed(Runnable {
                val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }, 200)
        }

        fun getFormattedTime(time: Date): String {
            var pattern = "dd/MM/yyyy"
            var simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())

            return simpleDateFormat.format(time)
        }

    }
}