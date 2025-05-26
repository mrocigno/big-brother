package br.com.mrocigno.bigbrother.common.converter

import androidx.room.TypeConverter

class ListStringConverter {

    @TypeConverter
    fun toListString(value: String): List<String> = value.split(", ")

    @TypeConverter
    fun toString(list: List<String>): String = list.joinToString(", ")
}
