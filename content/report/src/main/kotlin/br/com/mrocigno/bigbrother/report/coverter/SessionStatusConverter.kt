package br.com.mrocigno.bigbrother.report.coverter

import androidx.room.TypeConverter
import br.com.mrocigno.bigbrother.report.model.SessionStatus

internal class SessionStatusConverter {

    @TypeConverter
    fun toSessionStatus(status: String): SessionStatus = SessionStatus.valueOf(status)

    @TypeConverter
    fun toString(status: SessionStatus): String = status.name
}