package com.jeffrey.timer.extension

fun Long.toMinuteSecondFormat(): String {
    if (this < 0L)
        return "00:00"
    val min = (this / 60).toString().padStart(2, '0')
    val sec = this.rem(60).toString().padStart(2, '0')
    return "$min:$sec"
}