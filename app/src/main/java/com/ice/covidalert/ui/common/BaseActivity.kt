package com.ice.covidalert.ui.common

import android.app.Dialog
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.ice.covidalert.utils.CommonUtil

open class BaseActivity: FragmentActivity() {
    private var TAG = "BaseActivity"

    internal fun makeToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

//
    private var loadingDialog: Dialog? = null


    override fun onDestroy() {
        super.onDestroy()
        loadingDialog?.dismiss()
    }

    //region show loading
    fun showLoading() {
        if(loadingDialog == null) {
            loadingDialog = CommonUtil.makeLoadingDialog(this)
            loadingDialog?.setOnCancelListener {
                clickBack()
                hideLoading()
            }
        }
        hideLoading()
        loadingDialog?.show()
    }


    fun hideLoading() {
        loadingDialog?.hide()
        loadingDialog?.setOnKeyListener(null)
    }

    //endregion

    open fun clickBack() {
        Log.d(TAG, supportFragmentManager.backStackEntryCount.toString())
        if (isRootScreen()) {
            this.finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    protected fun isRootScreen(): Boolean {
        try {
            return supportFragmentManager.backStackEntryCount <= 1
        } catch (e: Exception) {
            return true
        }
    }
}