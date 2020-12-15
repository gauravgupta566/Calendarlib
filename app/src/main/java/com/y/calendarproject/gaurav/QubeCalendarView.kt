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

class QubeCalendarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    var list = mutableListOf<CalendarDateModel>()

    private var view: View? = null
    private var currentCalendar: Calendar? = null
    private var todayCalendar: Calendar? = null
    private var todayDate: Date? = null
    private var todayYear: Int? = null
    private var todayMonth: Int? = null
    private var defaultSelectedDate: Int? = null


    private var calendarAdapter = CalendarAdapter()
    private var selectedDate = 0
    private var monthChanges = 0
    private var monthIntervalGap = 0

    private var firstDate = 0
    private var secondDate = 0

    private var selectedDateYear=0
    private var selectedMonthYear=-1




    private val listener by lazy {

        object : CalendarAdapter.CalendarListener {
            override fun onceMonthSelected(date: Int) {
                selectedDate = date
                updateView()
                Log.d("hello", "updateviews called")
            }

            override fun twiceDateSelected(firstDay: Int, secondDay: Int) {
                Log.d("hello overide",CalendarUtils.selectedInterval.toString())

                firstDate = firstDay
                secondDate=secondDay
                updateView()
            }

            override fun everyTwoMonthSelected(date: Int) {
                monthIntervalGap = 2
                monthChanges = 2
                selectedDate = date
                updateView()
            }

            override fun quarterlySelected(date: Int) {
                monthIntervalGap = 4
                monthChanges = 4
                selectedDate = date
                updateView()
            }

            override fun sixMonthSelected(date: Int) {
                monthIntervalGap = 6
                monthChanges = 6
                selectedDate = date
                updateView()
            }

            override fun yearlySelected(date: Int,month:Int) {
             selectedDateYear=date
             selectedMonthYear=month
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
                if (CalendarUtils.selectedInterval!=12){
                    monthChanges--
                    if (monthChanges == 0) {
                        monthChanges = monthIntervalGap
                    }
                }
            }
            changeCalendarMonth(-1)
        }
        rightCalendarButton.setOnClickListener {
            if ((CalendarUtils.selectedInterval > 11) && (CalendarUtils.selectedInterval <= 15)) {
                if (CalendarUtils.selectedInterval!=12){
                    if (monthChanges == monthIntervalGap) {
                        monthChanges = 0
                    }
                    monthChanges++
                }
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

        dateText = dateText.substring(0, 1).toUpperCase(Locale.ROOT) + dateText.subSequence(
            1,
            dateText.length
        )


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
            monthTitleCalendar.text = dateText
        }
        var previousOrFuture = false
        if (selectedYear == todayYear) {
            if (selectedMonth < todayMonth!!) previousOrFuture = true
        }

        if (selectedYear < todayYear!!) previousOrFuture = true

        if (CalendarUtils.selectedFrequency == 1) {

            when (CalendarUtils.selectedInterval) {
                11 -> {
                    setDatesForOnceAMonth(maxdate, previousOrFuture)
                }
                12 -> {
                    setDatesForTwiceAMonth(maxdate, previousOrFuture)
                }
                13 -> {
                    setDatesForEveryTwoMonths(maxdate, previousOrFuture)
                }
                14 -> {
                    setDatesForQuarterly(maxdate, previousOrFuture)
                }
                15 -> {
                    setDatesForSixMonths(maxdate, previousOrFuture)
                }
            }
        }

        else if (CalendarUtils.selectedFrequency==2){
            when(CalendarUtils.selectedInterval){
                21 -> {
                    setDatesForWeekly(maxdate, previousOrFuture)
                }
                22 -> {
                    setDatesForTwoWeek(maxdate, previousOrFuture)
                }
            }
        }

        else{
            setDatesForYearly(maxdate, previousOrFuture)
        }
    }

    private fun setDatesForYearly(maxdate: Int, previousOrFuture: Boolean) {

        if (selectedMonthYear==-1){
            selectedMonthYear= currentCalendar!![Calendar.MONTH]
         }


        val highLightedDate = if (selectedDate > 0) {
            selectedDate
        } else {
            defaultSelectedDate!!
        }

        for (i in 1..maxdate) {
            if (highLightedDate == i &&selectedMonthYear==currentCalendar!![Calendar.MONTH]) {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, true)))
            } else {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, false)))
            }
        }
    }

    private fun setDatesForWeekly(maxdate: Int, previousOrFuture: Boolean) {
       val auxWeeklyCalendar= currentCalendar

        for (i in 1..maxdate) {
            auxWeeklyCalendar!![Calendar.DAY_OF_MONTH] = i
            val dayOfMonth = auxWeeklyCalendar[Calendar.DAY_OF_WEEK]
            Log.d("hello",dayOfMonth.toString())
            if (dayOfMonth==CalendarUtils.selectedday) {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, true)))
                Log.d("hello validation",dayOfMonth.toString())

            } else {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, false)))
            }
        }

    }
    private fun setDatesForTwoWeek(maxdate: Int, previousOrFuture: Boolean) {
        val auxWeeklyCalendar= currentCalendar
        var showSelecteDate=false
        for (i in 1..maxdate) {
            auxWeeklyCalendar!![Calendar.DAY_OF_MONTH] = i
            val dayOfMonth = auxWeeklyCalendar[Calendar.DAY_OF_WEEK]

            if (dayOfMonth==CalendarUtils.selectedday) {
                Log.d("hello day",dayOfMonth.toString())
                showSelecteDate=!showSelecteDate

                Log.d("hello boolean",showSelecteDate.toString()+" "+i.toString())

                if (showSelecteDate){
                    list.add((CalendarDateModel(i.toString(), previousOrFuture, true)))
                    Log.d("hello validation",dayOfMonth.toString())
                }
                else{
                    list.add((CalendarDateModel(i.toString(), previousOrFuture, false)))

                }

            } else {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, false)))
            }
        }
    }

    private fun setDatesForTwiceAMonth(maxdate: Int, previousOrFuture: Boolean) {
        for (i in 1..maxdate) {
            if (i==firstDate || i==secondDate ) {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, true)))
            } else {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, false)))
            }
        }
    }

    private fun setDatesForSixMonths(maxdate: Int, previousOrFuture: Boolean) {
        val highLightedDate = if (selectedDate > 0) {
            selectedDate
        } else {
            defaultSelectedDate!!
        }

        for (i in 1..maxdate) {
            if (highLightedDate == i && monthChanges == monthIntervalGap) {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, true)))
            } else {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, false)))
            }
        }
    }

    private fun setDatesForQuarterly(maxdate: Int, previousOrFuture: Boolean) {
        val highLightedDate = if (selectedDate > 0) {
            selectedDate
        } else {
            defaultSelectedDate!!
        }

        for (i in 1..maxdate) {
            if (highLightedDate == i && monthChanges == monthIntervalGap) {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, true)))
            } else {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, false)))
            }
        }
    }

    private fun setDatesForEveryTwoMonths(maxdate: Int, previousOrFuture: Boolean) {
        val highLightedDate = if (selectedDate > 0) {
            selectedDate
        } else {
            defaultSelectedDate!!
        }

        for (i in 1..maxdate) {
            if (highLightedDate == i && monthChanges == monthIntervalGap) {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, true)))
            } else {
                list.add((CalendarDateModel(i.toString(), previousOrFuture, false)))
            }
        }
    }

    private fun setDatesForOnceAMonth(maxdate: Int, previousOrFuture: Boolean) {
        val highLightedDate = if (selectedDate > 0) {
            selectedDate
        } else {
            defaultSelectedDate!!
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


