package com.y.calendarproject.gaurav

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.y.calendarproject.R
import kotlinx.android.synthetic.main.calendar_view_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

class QubeCalendarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    var list = ArrayList<String>()

    var view: View? = null
    var currentCalendar: Calendar? = null
    var todayCalendar: Calendar? = null
    var calendarAdapter: CalendarAdapter? = null


    init {
        view = LayoutInflater.from(context).inflate(R.layout.calendar_view_layout, this, true)
        loadDates(attrs)
    }

    private fun loadDates(attrs: AttributeSet?) {
        currentCalendar = Calendar.getInstance()
        todayCalendar = currentCalendar

        val day1 = currentCalendar?.get(Calendar.DAY_OF_WEEK)
        Log.d("hello first1", day1.toString())

        val day2 = currentCalendar?.get(Calendar.DAY_OF_MONTH)
        Log.d("hello first2", day2.toString())


        val auxCalendar =
            Calendar.getInstance(Locale.getDefault())
        auxCalendar.time = currentCalendar?.time
        auxCalendar[Calendar.DAY_OF_MONTH] = 1

        val firstDayOfMonth = auxCalendar[Calendar.DAY_OF_WEEK]
        Log.d("hello first3", firstDayOfMonth.toString())

        val maxdate = auxCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        Log.d("hello first4", maxdate.toString())

        auxCalendar.add(Calendar.MONTH, -1)
        val previousMaxdate = auxCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)


        //previous dates
       var dd= previousMaxdate -(firstDayOfMonth-2)
        for (i in dd..previousMaxdate) {
            list.add((i).toString())
        }

        // current dates
        for (i in 1..maxdate) {
            list.add((i).toString())
        }

        //futureDates
        for (i in 1..(35 - list.size)) {
            list.add((i).toString())
        }
        calendarAdapter = CalendarAdapter(list)
        calendarRecyclerView.adapter = calendarAdapter

    }

}