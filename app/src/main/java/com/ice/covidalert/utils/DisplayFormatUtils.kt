package com.ice.covidalert.utils

import android.app.Dialog
import android.content.Context
import androidx.annotation.LayoutRes
import com.ice.covidalert.R

class DisplayFormatUtils {

    companion object {
        fun makeLoadingDialog(context: Context): Dialog {
            return makeLoadingDialog(context, R.layout.dialog_loading)
        }

        fun makeLoadingDialog(context: Context, @LayoutRes layoutResID: Int): Dialog {
            val progressDialog = Dialog(context)
            progressDialog.setContentView(layoutResID)
            progressDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            progressDialog.setCanceledOnTouchOutside(false)
            return progressDialog
        }

        fun getDisplayMoneyValue(long: Long): String {
            return String.format("%.2f", long / 100.0)
        }

        fun getDigitsFromString(s: String): String {
            var temp: StringBuilder = StringBuilder()
            for (a in s) {
                if (a.isDigit())
                    temp.append(a)
            }
            return temp.toString()
        }

    }
}