package com.example.traders.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

object DateUtils {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCandleDate(daysBefore: Long): String {
        // ISO_LOCAL_DATE formats date to uuuu-mm-dd
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val date: LocalDate = LocalDate.now().minusDays(daysBefore)
        val text: String = date.format(formatter)
        return text
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentTime(): String {
        val date = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return date.format(formatter)
    }
}