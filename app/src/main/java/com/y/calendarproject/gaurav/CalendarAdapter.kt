package com.y.calendarproject.gaurav

import android.content.Context
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
    private var selectedTv: TextView? = null
    private var calendarListener: CalendarListener? = null
    private var firstDateTv: TextView? = null
    private var secondDateTv: TextView? = null
    private var cyclicTurn=""


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_days, parent, false)

        return CalendarViewHolder(view).apply {
            itemView.dayTv.setOnClickListener {
                setClick(itemView.dayTv, parent.context)
            }
        }

    }

    private fun setClick(dayTv: TextView?, context: Context) {
        if (CalendarUtils.selectedFrequency == CalendarUtils.monthlyFrequencyId) {
            if (CalendarUtils.selectedInterval == CalendarUtils.twiceAMonth) {
                setItemForTwiceAMonth(dayTv, context)
            } else {
                setItemForMonthly(dayTv, context)
            }
        } else if (CalendarUtils.selectedFrequency == CalendarUtils.weeklyFrequencyId) {

           if(dayTv?.background!=null){

              setItemForWeekly(dayTv, context)

           }
           else {

           }
            var a = 100

        } else {
            setItemForYearly(dayTv, context)
        }

    }

    private fun setItemForWeekly(dayTv: TextView, context: Context) {
        deSelectPreviousDay(dayTv,context)
        updateDates(dayTv.text.toString().toInt())

    }

    private fun setItemForYearly(dayTv: TextView?, context: Context) {
        if (selectedTv != null) {
            deSelectPreviousDay(selectedTv, context)
            selectedTv = dayTv
        } else {
            selectedTv = dayTv
        }
        dayTv?.setTextColor(ContextCompat.getColor(context, R.color.white))
        dayTv?.setBackgroundResource(R.drawable.bg_date_selected_bill_qube)
        updateDates(dayTv?.text.toString().toInt())
    }

    private fun setItemForTwiceAMonth(dayTv: TextView?, context: Context) {
        //disable previous dates
        if (selectedTv != null) {
            deSelectPreviousDay(selectedTv, context)
        }
        selectedTv = null

        if (firstDateTv == null) {
            firstDateTv = dayTv
        } else if (firstDateTv != null && secondDateTv == null) {
            secondDateTv = dayTv
            cyclicTurn="first"
        } else if (firstDateTv != null && secondDateTv != null) {

            if (cyclicTurn=="first"){
                //change firstDateTv
                deSelectPreviousDay(firstDateTv, context)
                firstDateTv = dayTv

                cyclicTurn="second"
            }
            else{
                //change secondDateTv
                deSelectPreviousDay(secondDateTv, context)
                secondDateTv = dayTv
                cyclicTurn="first"
            }
        }

        dayTv?.setTextColor(ContextCompat.getColor(context, R.color.white))
        dayTv?.setBackgroundResource(R.drawable.bg_date_selected_bill_qube)
        updateDates(dayTv?.text.toString().toInt())

    }

    private fun deSelectPreviousDay(
        dateTv: TextView?,
        context: Context
    ) {
        dateTv?.setTextColor(ContextCompat.getColor(context, R.color.gray3A))
        dateTv?.setBackgroundResource(0)
    }

    private fun setItemForMonthly(dayTv: TextView?, context: Context) {
        if (selectedTv != null) {
            selectedTv?.setTextColor(ContextCompat.getColor(context, R.color.gray3A))
            selectedTv?.setBackgroundResource(0)
            dayTv?.setTextColor(ContextCompat.getColor(context, R.color.white))
            selectedTv = dayTv
        } else {
            dayTv?.setTextColor(ContextCompat.getColor(context, R.color.white))
            selectedTv = dayTv
        }
        dayTv?.setBackgroundResource(R.drawable.bg_date_selected_bill_qube)
        updateDates(dayTv?.text.toString().toInt())
    }

    private fun updateDates(date: Int) {
        if (CalendarUtils.selectedFrequency == CalendarUtils.monthlyFrequencyId) {

            if (CalendarUtils.selectedInterval == 11) {
                calendarListener?.onceMonthSelected(date)
            } else if (CalendarUtils.selectedInterval == 12) {
                if (firstDateTv != null && secondDateTv != null) {
                    calendarListener?.twiceDateSelected(
                        firstDateTv?.text.toString().toInt(),
                        secondDateTv?.text.toString().toInt()
                    )
                } else if (firstDateTv != null && secondDateTv == null) {
                    //here we need to set error message for 2nd date
                   // calendarListener?.twiceDateSelected(firstDateTv?.text.toString().toInt(), 0)
                }
            } else if (CalendarUtils.selectedInterval == 13) {
                calendarListener?.everyTwoMonthSelected(date)
            } else if (CalendarUtils.selectedInterval == 14) {
                calendarListener?.quarterlySelected(date)
            } else if (CalendarUtils.selectedInterval == 15) {
                calendarListener?.sixMonthSelected(date)
            }
        } else if (CalendarUtils.selectedFrequency == CalendarUtils.weeklyFrequencyId) {
            if (CalendarUtils.selectedInterval==CalendarUtils.oneWeek){
                calendarListener?.everyWeekSelected(date)
            }
            else {
               calendarListener?.twoWeekSelected(date)
            }
        } else {
            calendarListener?.yearlySelected(date)
          }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val model = list[position]

        holder.itemView.dayTv.text = model.date
        if (model.previousOrFuture) {
            holder.itemView.dayTv.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.grayC7
                )
            )
            holder.itemView.dayTv.isClickable = false
            holder.itemView.dayTv.setBackgroundResource(0)
        } else {
            if (model.selectedDate) {
                holder.itemView.dayTv.setBackgroundResource(R.drawable.bg_date_selected_bill_qube)
                holder.itemView.dayTv.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.white
                    )
                )
                selectedTv = holder.itemView.dayTv
            } else {
                holder.itemView.dayTv.setBackgroundResource(0)
                holder.itemView.dayTv.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.gray3A
                    )
                )
            }
            holder.itemView.dayTv.isClickable = true
        }
    }

    fun populateDates(populateList: MutableList<CalendarDateModel>) {
        list.clear()
        list.addAll(populateList)

        notifyDataSetChanged()
    }

    class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    fun setCalendarListener(calendarListener: CalendarListener) {
        this.calendarListener = calendarListener
    }

    interface CalendarListener {
        //monthly
        fun onceMonthSelected(date: Int)
        fun twiceDateSelected(firstdate: Int, secondDate: Int)
        fun everyTwoMonthSelected(date: Int)
        fun quarterlySelected(date: Int)
        fun sixMonthSelected(date: Int)

        // weekly
        fun everyWeekSelected(date: Int)
        fun twoWeekSelected(date: Int)

        //yearly
        fun yearlySelected(date: Int)
    }
}
