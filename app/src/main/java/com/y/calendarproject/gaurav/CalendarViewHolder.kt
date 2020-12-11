package com.y.calendarproject.gaurav

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.y.calendarproject.R
import kotlinx.android.synthetic.main.item_days.view.*

class CalendarViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {


    fun bind(value:String){
        itemView.dayTv.text=value
    }

    companion object {
        fun create(parent: ViewGroup): CalendarViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_days, parent, false)
            return CalendarViewHolder(view)
        }
    }
}