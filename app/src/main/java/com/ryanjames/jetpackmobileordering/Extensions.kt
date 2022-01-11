package com.ryanjames.jetpackmobileordering

import java.util.*

fun Float.toTwoDigitString(): String {
    return String.format(Locale.US, "%.2f", this)
}

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

fun <T> MutableList<T>.clearAndAddAll(c: Collection<T>) {
    clear()
    addAll(c)
}