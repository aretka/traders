package com.example.traders.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentTime(): String {
        val date = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return date.format(formatter)
    }
}