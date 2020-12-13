package com.y.calendarproject.gaurav

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_days.view.*

class CalendarAdapter : RecyclerView.Adapter<CalendarViewHolder>() {

    private var list = mutableListOf<CalendarDateModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        return CalendarViewHolder.create(parent).apply {
            itemView.dayTv.setOnClickListener {
            Log.d("hello",itemView.dayTv.text.toString())
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun populateDates(populateList: MutableList<CalendarDateModel>) {
        list.clear()
        list.addAll(populateList)
        notifyDataSetChanged()
    }
}