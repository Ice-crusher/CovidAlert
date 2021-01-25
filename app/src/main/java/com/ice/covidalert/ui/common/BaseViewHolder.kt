package com.ice.covidalert.ui.common

import android.view.View
import androidx.recyclerview.widget.RecyclerView


open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var currentPosition: Int = 0
        private set

    open fun onBind(position: Int) {
        currentPosition = position
    }
}