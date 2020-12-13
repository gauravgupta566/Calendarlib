package com.y.calendarproject.gaurav

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.y.calendarproject.R
import kotlinx.android.synthetic.main.calendar_month_layout.view.*
import kotlinx.android.synthetic.main.calendar_view_layout.view.*
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.ArrayList

class QubeCalendarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    var list = mutableListOf<CalendarDateModel>()

    var view: View? = null
    var currentCalendar: Calendar? = null
    var todayCalendar: Calendar? = null
    var todayDate: Date? = null
    var todayYear: Int? = null
    var todayMonth: Int? = null


    var calendarAdapter= CalendarAdapter()


    init {
        view = LayoutInflater.from(context).inflate(R.layout.calendar_view_layout, this, true)
        calendarRecyclerView.adapter = calendarAdapter
        getCurrentCalendarDetails()

    }

    private fun getCurrentCalendarDetails() {
        currentCalendar = Calendar.getInstance()
        todayCalendar = currentCalendar
        todayDate = todayCalendar?.time
        todayYear = todayCalendar?.get(Calendar.YEAR)
        todayMonth = todayCalendar?.get(Calendar.MONTH)

        leftCalendarButton.setOnClickListener {
           changeCalendarMonth(-1)
        }
        rightCalendarButton.setOnClickListener {
            changeCalendarMonth(1)
        }
        updateView()
    }

    private fun changeCalendarMonth(i: Int) {
        currentCalendar?.add(Calendar.MONTH, i)
        updateView()
    }

    private fun updateView() {

        list.clear()


        val auxCalendar =
            Calendar.getInstance(Locale.getDefault())
        auxCalendar.time = currentCalendar?.time
        auxCalendar[Calendar.DAY_OF_MONTH] = 1

        val firstDayOfMonth = auxCalendar[Calendar.DAY_OF_WEEK]

        val maxdate = auxCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val selectedYear=auxCalendar.get(Calendar.YEAR)
        val selectedMonth=auxCalendar.get(Calendar.MONTH)
        val selectedDate=auxCalendar.time
        var dateText = DateFormatSymbols(Locale.getDefault()).months[currentCalendar!![Calendar.MONTH]]

        dateText = dateText.substring(0, 1).toUpperCase() + dateText.subSequence(1, dateText.length)


        auxCalendar.add(Calendar.MONTH, -1)
        val previousMaxdate = auxCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)


        //previous dates
        val previousDateStart = previousMaxdate - (firstDayOfMonth - 2)
        for (i in previousDateStart..previousMaxdate) {
            list.add((CalendarDateModel(i.toString(),true)))
        }

        // current dates
       if (selectedYear!=todayYear){
           monthTitleCalendar.text= "$dateText $selectedYear"
       }
      else {
           monthTitleCalendar.text= "$dateText"
       }
        var previousOrFuture=false
        if (selectedYear==todayYear){
            if (selectedMonth==todayMonth)
            previousOrFuture=true
        }
        for (i in 1..maxdate) {
            list.add((CalendarDateModel(i.toString(),previousOrFuture)))
        }

        var daysCount = 42
        if (firstDayOfMonth < 5) {
            daysCount = 35
        }
        //futureDates
        for (i in 1..(daysCount - list.size)) {
            list.add((CalendarDateModel(i.toString(),true)))
        }
        calendarAdapter.populateDates(list)
    }

}