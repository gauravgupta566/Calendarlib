package com.y.calendarproject.gaurav

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.y.calendarproject.R
import kotlinx.android.synthetic.main.item_days.view.*

class CalendarViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {


    fun bind(model:CalendarDateModel){
        itemView.dayTv.text=model.date
        if (model.previousOrFuture){
            itemView.dayTv.setTextColor(ContextCompat.getColor(itemView.context,R.color.greyed_out))
            itemView.dayTv.isClickable=false
        }
        else{
            itemView.dayTv.setTextColor(ContextCompat.getColor(itemView.context,R.color.grey))

        }
    }

    companion object {
        fun create(parent: ViewGroup): CalendarViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_days, parent, false)
            return CalendarViewHolder(view)
        }
    }
}