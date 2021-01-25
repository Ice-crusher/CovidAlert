package com.ice.covidalert.ui

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ice.covidalert.R
import com.ice.covidalert.ui.common.BaseViewHolder
import com.ice.domain.models.HistoryModel
import kotlinx.android.synthetic.main.item_history.view.*
import java.text.SimpleDateFormat

class HistoryAdapter: RecyclerView.Adapter<BaseViewHolder>() {
    val TAG = "AnimalsAdapter"

    private val VIEW_TYPE_NORMAL = 1
//    private val VIEW_TYPE_GROUP_HEADER = 2

    private var items = ArrayList<HistoryModel.HistoryItem>();

    private var onClickItemListener: OnClickItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
//        if (viewType == VIEW_TYPE_NORMAL)
        return NormalViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_history, parent, false))
//            )
    }

    override fun getItemViewType(position: Int): Int {
//        if (transactions.data.get(position).type == TransactionsListItem.TYPE_ITEM)
        return VIEW_TYPE_NORMAL
//        else
//            return VIEW_TYPE_GROUP_HEADER
    }

    override fun getItemCount(): Int {
        Log.d(TAG,"$items.size")
        return items.size
    }

    fun setItems(items: ArrayList<HistoryModel.HistoryItem>) {
        this.items.clear()
        this.items = items
        notifyDataSetChanged()
    }

    fun addItems(items: ArrayList<HistoryModel.HistoryItem>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun getItem(pos: Int): HistoryModel.HistoryItem {
        return items[pos]
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
//        if (position >= itemCount - 4)
//            onBottomReachedListener?.onBottomReached()
        holder.onBind(position)
    }

    fun setClickListener(onClickItemListener: OnClickItemListener) {
        this.onClickItemListener = onClickItemListener
    }

    inner class NormalViewHolder(view: View) : BaseViewHolder(view) {

        @SuppressLint("SetTextI18n")
        override fun onBind(position: Int) {
            super.onBind(position)
            itemView.itemButtonMap.setOnClickListener {
                onClickItemListener?.onClick(items[position])
            }

            if (items[position].geographicCoordinateX == null ||
                items[position].geographicCoordinateY == null
            ) {
                itemView.itemLayoutMap.visibility = View.GONE
            } else {
                itemView.itemLayoutMap.visibility = View.VISIBLE
            }

            items[position].geographicCoordinateX?.let {
                itemView.itemCoordX.text = "latitude: $it"
            }

            items[position].geographicCoordinateY?.let {
                itemView.itemCoordY.text = "longitude: $it"
            }

            val dateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
            itemView.itemDate.text = dateFormat.format(items[position].time)
        }
    }

    interface OnClickItemListener {
        fun onClick(item: HistoryModel.HistoryItem)
    }

    fun Double.format(digits: Int) = "%.${digits}f".format(this)
}