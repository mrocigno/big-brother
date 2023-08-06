package br.com.mrocigno.bigbrother.report.coverter

import androidx.room.TypeConverter
import br.com.mrocigno.bigbrother.report.model.ReportType

internal class ReportTypeConverter {

    @TypeConverter
    fun toReportType(type: String): ReportType = ReportType.valueOf(type)

    @TypeConverter
    fun toString(type: ReportType): String = type.name
}