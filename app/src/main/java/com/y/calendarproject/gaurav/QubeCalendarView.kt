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
    var defaultSelectedDate: Int? = null


    var calendarAdapter = CalendarAdapter()
    var selectedDate = 0
    var monthChanges = 0
    var monthIntervalGap = 0

    val listener by lazy {

        object : CalendarAdapter.CalendarListener {
            override fun onceMonthSelected(date: Int) {
                selectedDate = date
                updateView()
                Log.d("hello", "updateviews called")
            }

            override fun twiceDateSelected(firstdate: Int, secondDate: Int) {
                updateView()
            }

            override fun everyTwoMonthSelected(date: Int) {
                monthIntervalGap=2
                monthChanges=2
                selectedDate = date
                updateView()
            }

            override fun quarterlySelected(date: Int) {
                monthIntervalGap=4
                monthChanges=4
                selectedDate = date
                updateView()
            }

            override fun sixMonthSelected(date: Int) {
                monthIntervalGap=6
                monthChanges=6
                selectedDate = date
                updateView()
            }
        }
    }


    init {
        view = LayoutInflater.from(context).inflate(R.layout.calendar_view_layout, this, true)
        calendarRecyclerView.adapter = calendarAdapter
        calendarAdapter.setCalendarListener(listener)
        getCurrentCalendarDetails()

    }

    private fun getCurrentCalendarDetails() {
        currentCalendar = Calendar.getInstance()
        todayCalendar = currentCalendar
        todayDate = todayCalendar?.time
        todayYear = todayCalendar?.get(Calendar.YEAR)
        todayMonth = todayCalendar?.get(Calendar.MONTH)
        defaultSelectedDate = todayCalendar?.get(Calendar.DATE)

        leftCalendarButton.setOnClickListener {
            if ((CalendarUtils.selectedInterval > 11) && (CalendarUtils.selectedInterval <= 15)) {

                monthChanges--
                    if (monthChanges == 0) {
                        monthChanges = monthIntervalGap
                    }


            }

            changeCalendarMonth(-1)
        }
        rightCalendarButton.setOnClickListener {
            if ((CalendarUtils.selectedInterval > 11) && (CalendarUtils.selectedInterval <= 15)) {
                if (monthChanges == monthIntervalGap) {
                    monthChanges = 0
                }
                monthChanges++
            }

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

        val selectedYear = auxCalendar.get(Calendar.YEAR)
        val selectedMonth = auxCalendar.get(Calendar.MONTH)
        var dateText =
            DateFormatSymbols(Locale.getDefault()).months[currentCalendar!![Calendar.MONTH]]

        dateText = dateText.substring(0, 1).toUpperCase() + dateText.subSequence(1, dateText.length)


        auxCalendar.add(Calendar.MONTH, -1)
        val previousMaxdate = auxCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)


        //previous dates
        setPreviousDays(previousMaxdate, firstDayOfMonth)

        // current dates
        setCurrentdays(selectedYear, dateText, selectedMonth, maxdate)

        //futureDates
        setFutureDays(firstDayOfMonth)

        calendarAdapter.populateDates(list)
    }

    private fun setFutureDays(firstDayOfMonth: Int) {
        var daysCount = 42
        if (firstDayOfMonth < 4) {
            daysCount = 35
        }
        for (i in 1..(daysCount - list.size)) {
            list.add((CalendarDateModel(i.toString(), true, false)))
        }
    }

    private fun setPreviousDays(previousMaxdate: Int, firstDayOfMonth: Int) {
        val previousDateStart = previousMaxdate - (firstDayOfMonth - 2)
        for (i in previousDateStart..previousMaxdate) {
            list.add((CalendarDateModel(i.toString(), true, false)))
        }
    }

    private fun setCurrentdays(
        selectedYear: Int,
        dateText: String,
        selectedMonth: Int,
        maxdate: Int
    ) {
        if (selectedYear != todayYear) {
            monthTitleCalendar.text = "$dateText $selectedYear"
        } else {
            monthTitleCalendar.text = "$dateText"
        }
        var previousOrFuture = false
        if (selectedYear == todayYear) {
            if (selectedMonth < todayMonth!!) previousOrFuture = true
        }

        if (selectedYear < todayYear!!) previousOrFuture = true

        if (CalendarUtils.selectedFrequency == 1) {

            if (CalendarUtils.selectedInterval == 11) {
                setDatesForOnceAMonth(maxdate, previousOrFuture)
            } else if (CalendarUtils.selectedInterval == 12) {
                setDatesForTwiceAMonth(maxdate, previousOrFuture)
            } else if (CalendarUtils.selectedInterval == 13) {
                setDatesForEveryTwoMonths(maxdate, previousOrFuture)
            } else if (CalendarUtils.selectedInterval == 14) {
                setDatesForQuarterly(maxdate, previousOrFuture)
            } else if (CalendarUtils.selectedInterval == 15) {
                setDatesForSixMonths(maxdate, previousOrFuture)
            }
        }
    }

    private fun setDatesForTwiceAMonth(maxdate: Int, previousOrFuture: Boolean) {

    }

    private fun setDatesForSixMonths(maxdate: Int, previousOrFuture: Boolean) {
        var highLightedDate = 0
        if (selectedDate > 0) {
            highLightedDate = selectedDate
        } else {
            highLightedDate = defaultSelectedDate!!
        }

        for (i in 1..maxdate) {
            if (highLightedDate == i &&monthChanges==monthIntervalGap) {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, true)))
            } else {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, false)))
            }
        }
    }

    private fun setDatesForQuarterly(maxdate: Int, previousOrFuture: Boolean) {
        var highLightedDate = 0
        if (selectedDate > 0) {
            highLightedDate = selectedDate
        } else {
            highLightedDate = defaultSelectedDate!!
        }

        for (i in 1..maxdate) {
            if (highLightedDate == i &&monthChanges==monthIntervalGap) {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, true)))
            } else {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, false)))
            }
        }
    }

    private fun setDatesForEveryTwoMonths(maxdate: Int, previousOrFuture: Boolean) {
        Log.d("hello","2 months")
        var highLightedDate = 0
        if (selectedDate > 0) {
            highLightedDate = selectedDate
        } else {
            highLightedDate = defaultSelectedDate!!
        }

        for (i in 1..maxdate) {
            if (highLightedDate == i && monthChanges==monthIntervalGap) {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, true)))
            } else {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, false)))
            }
        }
    }

    private fun setDatesForOnceAMonth(maxdate: Int, previousOrFuture: Boolean) {
        var highLightedDate = 0
        if (selectedDate > 0) {
            highLightedDate = selectedDate
        } else {
            highLightedDate = defaultSelectedDate!!
        }

        for (i in 1..maxdate) {
            if (highLightedDate == i) {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, true)))
            } else {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, false)))
            }
        }
    }

}




