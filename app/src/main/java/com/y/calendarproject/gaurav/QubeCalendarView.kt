package com.y.calendarproject.gaurav

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
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
    private var todayMonthStartDate=0

    private var defaultSelectedDate: Int? = null


    private var calendarAdapter = CalendarAdapter()
    private var selectedDate = 0
    private var monthChanges = 0
    private var monthIntervalGap = 0

    private var firstDate = 0
    private var secondDate = 0

    private var selectedDateForYearlyFrequency = 0
    private var selectedMonthYearlyFrequency = -1


    private val listener by lazy {

        object : CalendarAdapter.CalendarListener {
            override fun onceMonthSelected(date: Int) {
                selectedDate = date
                updateView()
                Log.d("hello", "updateviews called")
            }

            override fun twiceDateSelected(firstDay: Int, secondDay: Int) {
                Log.d("hello overide", CalendarUtils.selectedInterval.toString())

                firstDate = firstDay
                secondDate = secondDay
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

            override fun yearlySelected(date: Int) {
                selectedDateForYearlyFrequency = date
                selectedMonthYearlyFrequency = currentCalendar!![Calendar.MONTH]
                updateView()
            }
        }
    }


    init {
        view = LayoutInflater.from(context).inflate(R.layout.calendar_view_layout, this, true)
        calendarRecyclerView.adapter = calendarAdapter
        calendarAdapter.setCalendarListener(listener)
        getCurrentCalendarDetails(context)

    }

    private fun getCurrentCalendarDetails(context: Context) {
        currentCalendar = Calendar.getInstance()
        todayCalendar = currentCalendar
        todayDate = todayCalendar?.time
        todayYear = todayCalendar?.get(Calendar.YEAR)
        todayMonth = todayCalendar?.get(Calendar.MONTH)
        defaultSelectedDate = todayCalendar?.get(Calendar.DATE)

        leftCalendarButton.setOnClickListener {

            //set Calendar Title
            if ((currentCalendar!![Calendar.YEAR] == todayYear) && (currentCalendar!![Calendar.MONTH] == todayMonth)) {
                Toast.makeText(context, "You cannot go to previous month", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if ((CalendarUtils.selectedInterval > 11) && (CalendarUtils.selectedInterval <= 15)) {
                if (CalendarUtils.selectedInterval != 12) {
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
                if (CalendarUtils.selectedInterval != 12) {
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
      val month=  currentCalendar?.get(Calendar.MONTH)

        Log.d("hello",month.toString())
        updateView()
    }

    private fun updateView() {
        list.clear()

        val auxCalendar =
            Calendar.getInstance(Locale.getDefault())
        auxCalendar.time = currentCalendar?.time

        if (todayMonthStartDate==0){
            todayMonthStartDate=auxCalendar[Calendar.DAY_OF_MONTH]
       }

        auxCalendar[Calendar.DAY_OF_MONTH] = 1

        val firstDayOfMonth = auxCalendar[Calendar.DAY_OF_WEEK]

        val maxdate = auxCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val selectedYear = auxCalendar.get(Calendar.YEAR)

        //set Calendar Month Title

        var dateText =
            DateFormatSymbols(Locale.getDefault()).months[currentCalendar!![Calendar.MONTH]]

        dateText = dateText.substring(0, 1).toUpperCase(Locale.ROOT) + dateText.subSequence(
            1,
            dateText.length
        )
        if (selectedYear != todayYear) {
            monthTitleCalendar.text = "$dateText $selectedYear"
        } else {
            monthTitleCalendar.text = dateText
        }


        auxCalendar.add(Calendar.MONTH, -1)
        val previousMaxdate = auxCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        //Previous Dates
        setPreviousMonthLastDays(previousMaxdate, firstDayOfMonth)

        // Current Month Dates
        setCurrentMonthDays(maxdate)

        //Upcoming Dates
        setUpcomingMonthDays(firstDayOfMonth)

        calendarAdapter.populateDates(list)
    }

    private fun setUpcomingMonthDays(firstDayOfMonth: Int) {
        var daysCount = 42
        if (firstDayOfMonth < 4) {
            daysCount = 35
        }
        for (i in 1..(daysCount - list.size)) {
            list.add((CalendarDateModel(i.toString(), true, false)))
        }
    }

    private fun setPreviousMonthLastDays(previousMaxdate: Int, firstDayOfMonth: Int) {
        val previousDateStart = previousMaxdate - (firstDayOfMonth - 2)
        for (i in previousDateStart..previousMaxdate) {
            list.add((CalendarDateModel(i.toString(), true, false)))
        }
    }

    private fun setCurrentMonthDays(
        maxdate: Int
    ) {

        //disable past date for current month

       /* if ((currentCalendar!![Calendar.MONTH] == todayMonth) && (currentCalendar!![Calendar.YEAR] == todayYear)) {
            if ((todayMonthStartDate-1)>1) {
                for (i in 1..(todayMonthStartDate - 1)) {
                    list.add(CalendarDateModel(i.toString(), true, false))
                }
            }
        }*/

        val startDate=1
        /* var previousOrFuture = false
         if (selectedYear == todayYear) {
             if (selectedMonth < todayMonth!!) previousOrFuture = true
         }

         if (selectedYear < todayYear!!) previousOrFuture = true
         */

        if (CalendarUtils.selectedFrequency == 1) {

            when (CalendarUtils.selectedInterval) {
                11 -> {
                    setDatesForOnceAMonth(startDate!!, maxdate)
                }
                12 -> {
                    setDatesForTwiceAMonth(startDate!!, maxdate)
                }
                13 -> {
                    setDatesForEveryTwoMonths(startDate!!, maxdate)
                }
                14 -> {
                    setDatesForQuarterly(startDate!!, maxdate)
                }
                15 -> {
                    setDatesForSixMonths(startDate!!, maxdate)
                }
            }
        } else if (CalendarUtils.selectedFrequency == 2) {
            when (CalendarUtils.selectedInterval) {
                21 -> {
                    setDatesForWeekly(startDate!!, maxdate)
                }
                22 -> {
                    setDatesForTwoWeek(startDate!!, maxdate)
                }
            }
        } else {
            setDatesForYearly(startDate!!, maxdate)
        }
    }


    private fun setDatesForYearly(startDate: Int, maxdate: Int) {

        if (selectedMonthYearlyFrequency == -1) {
            selectedMonthYearlyFrequency = currentCalendar!![Calendar.MONTH]
        }

        val highLightedDate = if (selectedDateForYearlyFrequency > 0) {
            selectedDateForYearlyFrequency
        } else {
            defaultSelectedDate!!
        }


        for (i in startDate..maxdate) {
            if (i == highLightedDate && selectedMonthYearlyFrequency == currentCalendar!![Calendar.MONTH]) {
                list.add((CalendarDateModel(i.toString(), false, true)))
            } else {
                list.add((CalendarDateModel(i.toString(), false, false)))
            }
        }
    }

    private fun setDatesForWeekly(startDate: Int, maxdate: Int) {
        val auxWeeklyCalendar = currentCalendar

        for (i in startDate..maxdate) {
            auxWeeklyCalendar!![Calendar.DAY_OF_MONTH] = i
            val dayOfMonth = auxWeeklyCalendar[Calendar.DAY_OF_WEEK]
            if (dayOfMonth == CalendarUtils.selectedday) {
                list.add((CalendarDateModel(i.toString(), false, true)))
            } else {
                list.add((CalendarDateModel(i.toString(), false, false)))
            }
        }

    }

    private fun setDatesForTwoWeek(startDate: Int, maxdate: Int) {
        val auxWeeklyCalendar = currentCalendar
        var showSelecteDate = false
        for (i in startDate..maxdate) {
            auxWeeklyCalendar!![Calendar.DAY_OF_MONTH] = i
            val dayOfMonth = auxWeeklyCalendar[Calendar.DAY_OF_WEEK]

            if (dayOfMonth == CalendarUtils.selectedday) {
                showSelecteDate = !showSelecteDate

                if (showSelecteDate) {
                    list.add((CalendarDateModel(i.toString(), false, true)))
                } else {
                    list.add((CalendarDateModel(i.toString(), false, false)))
                }

            } else {
                list.add((CalendarDateModel(i.toString(), false, false)))
            }
        }
    }

    private fun setDatesForTwiceAMonth(startDate: Int, maxdate: Int) {
        for (i in startDate..maxdate) {
            if (i == firstDate || i == secondDate) {
                list.add((CalendarDateModel(i.toString(), false, true)))
            } else {
                list.add((CalendarDateModel(i.toString(), false, false)))
            }
        }
    }

    private fun setDatesForSixMonths(startDate: Int, maxdate: Int) {
        val highLightedDate = if (selectedDate > 0) {
            selectedDate
        } else {
            defaultSelectedDate!!
        }

        for (i in startDate..maxdate) {
            if (highLightedDate == i && monthChanges == monthIntervalGap) {
                list.add((CalendarDateModel(i.toString(), false, true)))
            } else {
                list.add((CalendarDateModel(i.toString(), false, false)))
            }
        }
    }

    private fun setDatesForQuarterly(startDate: Int, maxdate: Int) {
        val highLightedDate = if (selectedDate > 0) {
            selectedDate
        } else {
            defaultSelectedDate!!
        }

        for (i in startDate..maxdate) {
            if (highLightedDate == i && monthChanges == monthIntervalGap) {
                list.add((CalendarDateModel(i.toString(), false, true)))
            } else {
                list.add((CalendarDateModel(i.toString(), false, false)))
            }
        }
    }

    private fun setDatesForEveryTwoMonths(startDate: Int, maxdate: Int) {
        val highLightedDate = if (selectedDate > 0) {
            selectedDate
        } else {
            defaultSelectedDate!!
        }

        for (i in startDate..maxdate) {
            if (highLightedDate == i && monthChanges == monthIntervalGap) {
                list.add((CalendarDateModel(i.toString(), false, true)))
            } else {
                list.add((CalendarDateModel(i.toString(), false, false)))
            }
        }
    }

    private fun setDatesForOnceAMonth(startDate: Int, maxdate: Int) {
        val highLightedDate = if (selectedDate > 0) {
            selectedDate
        } else {
            defaultSelectedDate!!
        }
        for (i in startDate..maxdate) {
            if (highLightedDate == i) {
                list.add((CalendarDateModel(i.toString(), false, true)))
            } else {
                list.add((CalendarDateModel(i.toString(), false, false)))
            }
        }
    }

}


