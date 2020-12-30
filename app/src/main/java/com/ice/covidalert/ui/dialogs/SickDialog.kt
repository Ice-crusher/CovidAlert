package com.ice.covidalert.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.ice.covidalert.R
import com.ice.covidalert.databinding.DialogSickBinding


class SickDialog(context: Context) : Dialog(context) {

    var listener: View.OnClickListener? = null
    private lateinit var binding: DialogSickBinding

    companion object {
        fun newInstance(context: Context, clickYesListener: View.OnClickListener): SickDialog? {
            return SickDialog(context).also {
                it.listener = clickYesListener
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = DialogSickBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.setOnClickListener { this.hide() }

        binding.buttonSickYes.setOnClickListener { buttonYes ->
            this.hide()
            listener?.onClick(buttonYes)
        }

        binding.buttonCancel.setOnClickListener {
            this.hide()
        }
    }

    override fun show() {
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        super.show()
    }
}