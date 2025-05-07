package br.com.mrocigno.bigbrother.report

import android.util.Log
import br.com.mrocigno.bigbrother.common.BBTAG
import br.com.mrocigno.bigbrother.common.dao.ReportLogDao
import br.com.mrocigno.bigbrother.common.dao.SessionDao
import br.com.mrocigno.bigbrother.common.db.BigBrotherDatabase.Companion.bbdb
import br.com.mrocigno.bigbrother.common.utils.bbSessionId
import br.com.mrocigno.bigbrother.report.model.ReportLogEntry
import br.com.mrocigno.bigbrother.report.model.ReportType
import br.com.mrocigno.bigbrother.report.model.SessionEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

object BigBrotherReport {

    private val job: Job = Job()
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + job)
    private val sessionDao: SessionDao? get() = bbdb?.sessionDao()
    private val reportDao: ReportLogDao? get() = bbdb?.reportLogDao()

    internal var nestedLevel = 0

    fun track(type: ReportType, content: String, nestedLevel: Int) = scope.launch {
        _track(type, content, nestedLevel)
    }.let { }

    private suspend fun _track(
        type: ReportType,
        content: String,
        nestedLevel: Int = this.nestedLevel
    ) {
        val entity = ReportLogEntry(
            type = type,
            txtContent = content,
            nestedLevel = nestedLevel
        )
        reportDao?.add(entity.toEntity())
    }

    fun trackCrash(e: Throwable) = scope.launch {
        sessionDao?.sessionCrashed(bbSessionId)
        _track(ReportType.CRASH, "X CRASH - ${e.message ?: "without message"}")
    }.let { }

    fun getSessionTimeline(sessionId: Long) = flow {
        val report = reportDao?.getSession(sessionId)
            ?.map(::ReportLogEntry)
            ?.buildReport()
            ?.generate()

        emit(report)
    }.flowOn(Dispatchers.IO)

    fun deleteCurrentSession() = scope.launch {
        Log.d(BBTAG, "Deleting session $bbSessionId")
        sessionDao?.deleteSession(bbSessionId)
    }

    internal fun listSessions(dateFilter: LocalDate?) = if (dateFilter != null) {
        val startDate = dateFilter.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "T00:00:00"
        val endDate = dateFilter.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "T23:59:59"
        sessionDao?.getSessionByRange(startDate, endDate)
    } else {
        sessionDao?.getAllSessions()
    }?.map { data ->
        data.map(::SessionEntry)
    }

    internal fun createSession() {
        sessionDao?.closePreviousSession()
        bbSessionId = sessionDao?.create(
            SessionEntry().toEntity()
        ) ?: -1L
    }
}

fun bbTrack(
    type: ReportType,
    nestedLevel: Int = BigBrotherReport.nestedLevel,
    content: () -> String
) = bbTrack(type, content.invoke(), nestedLevel)

fun bbTrack(
    type: ReportType,
    content: String,
    nestedLevel: Int = BigBrotherReport.nestedLevel
) = BigBrotherReport.track(type, content, nestedLevel)