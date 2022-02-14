package com.ryanjames.composemobileordering

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

fun <T> List<T>.replaceOrAdd(newValue: T, block: (T) -> Boolean): List<T> {
    if (find { block(it) } == null) {
        return this.plus(newValue)
    }
    return map {
        if (block(it)) newValue else it
    }
}