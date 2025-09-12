package com.vladabur.wubbalubba.presentation.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val DEFAULT_DATE_FORMAT = "MM/d/yyyy"

fun Date.getFullDate(): String {
    val dateFormatter =
        SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault())
    return dateFormatter.format(this)
}