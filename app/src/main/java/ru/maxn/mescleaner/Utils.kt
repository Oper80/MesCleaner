package ru.maxn.mescleaner

import java.util.*

const val SECONDS = 1000L
const val MINUTES = 60 * SECONDS
const val HOURS = 60 * MINUTES
const val DAYS = 24 * HOURS

object Utils {
    fun diffDays(date: Long):Int{
        return ((Date().time - date)/ DAYS).toInt()
    }
}
