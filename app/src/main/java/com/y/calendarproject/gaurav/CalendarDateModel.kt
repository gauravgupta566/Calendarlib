package com.y.calendarproject.gaurav

data class CalendarDateModel(
    var date: String,
    var previousOrFuture: Boolean = false,
    var selectedDate: Boolean = false
)
