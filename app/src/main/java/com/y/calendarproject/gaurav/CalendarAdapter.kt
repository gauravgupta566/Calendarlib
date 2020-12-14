package com.y.calendarproject.gaurav

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.y.calendarproject.R
import kotlinx.android.synthetic.main.item_days.view.*

 class CalendarAdapter : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var list = mutableListOf<CalendarDateModel>()
    private var selectedTv: TextView?=null
     private var calendarListener:CalendarListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_days, parent, false)

        return CalendarViewHolder(view).apply {
            itemView.dayTv.setOnClickListener {
                if (selectedTv!=null){
                    selectedTv?.setTextColor(ContextCompat.getColor(parent.context,R.color.grey))
                    selectedTv?.setBackgroundResource(0)
                    itemView.dayTv.setTextColor(ContextCompat.getColor(parent.context,R.color.white))
                    selectedTv=itemView.dayTv
                }
                else{
                    itemView.dayTv.setTextColor(ContextCompat.getColor(parent.context,R.color.white))
                    selectedTv=itemView.dayTv
                }
                itemView.dayTv.setBackgroundResource(R.drawable.bg_date_selected)
                Log.d("hello selecteddate",itemView.dayTv.text.toString())
                updateDates(itemView.dayTv.text.toString().toInt())
            }
        }

    }

     private fun updateDates(date: Int) {

         if (CalendarUtils.selectedFrequency==1){

             Log.d("hello",CalendarUtils.selectedInterval.toString())

             if (CalendarUtils.selectedInterval==11){
                 calendarListener?.onceMonthSelected(date)
             }
              else if (CalendarUtils.selectedInterval==12){
                 calendarListener?.twiceDateSelected(date,date)
             }

             else if (CalendarUtils.selectedInterval==13){
                 calendarListener?.everyTwoMonthSelected(date)
             }

             else if (CalendarUtils.selectedInterval==14){
                 calendarListener?.quarterlySelected(date)
             }

             else if (CalendarUtils.selectedInterval==15){
                 calendarListener?.sixMonthSelected(date)
             }
         }

     }

     override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
      val model= list[position]

        holder.itemView.dayTv.text=model.date
        if (model.previousOrFuture){
            holder.itemView.dayTv.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.greyed_out))
            holder.itemView.dayTv.isClickable=false
        }
        else{

            if (model.selectedDate){
                holder.itemView.dayTv.background= ContextCompat.getDrawable(holder.itemView.context,R.drawable.bg_date_selected)
                holder.itemView.dayTv.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.white))
              //  holder.itemView.dayTv.setPadding(12 ,12,12,12)
                selectedTv=holder.itemView.dayTv

            }
            else{
                holder.itemView.dayTv.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.grey))

            }
            holder.itemView.dayTv.isClickable=true

        }
     }

    fun populateDates(populateList: MutableList<CalendarDateModel>) {
        list.clear()
        list.addAll(populateList)
        notifyDataSetChanged()
    }

     class CalendarViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)


    fun setCalendarListener(calendarListener: CalendarListener){
        this.calendarListener=calendarListener

     }
     interface CalendarListener{
         fun onceMonthSelected(date:Int)
         fun twiceDateSelected(firstdate:Int,secondDate:Int)
         fun everyTwoMonthSelected(date:Int)
         fun quarterlySelected(date:Int)
         fun sixMonthSelected(date:Int)




     }
}