package com.y.calendarproject.gaurav

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_days.view.*

class CalendarAdapter(var list:ArrayList<String>) : RecyclerView.Adapter<CalendarViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        return CalendarViewHolder.create(parent).apply {
            itemView.dayTv.setOnClickListener {

            }
        }

    }

    override fun getItemCount(): Int {
      return list.size
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(list[position])
    }
}