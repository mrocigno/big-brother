package br.com.mrocigno.bigbrother.session.coverter

import androidx.room.TypeConverter
import org.threeten.bp.LocalDateTime

internal class LocalDateTimeConverter {

    @TypeConverter
    fun toDate(date: String): LocalDateTime = LocalDateTime.parse(date)

    @TypeConverter
    fun toDateString(date: LocalDateTime): String = date.toString()
}