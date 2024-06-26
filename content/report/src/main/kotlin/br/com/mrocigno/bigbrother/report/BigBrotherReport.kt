package br.com.mrocigno.bigbrother.report

import android.content.Context
import android.util.Log
import androidx.room.Room
import br.com.mrocigno.bigbrother.common.BBTAG
import br.com.mrocigno.bigbrother.core.utils.bbSessionId
import br.com.mrocigno.bigbrother.report.entity.ReportLogEntity
import br.com.mrocigno.bigbrother.report.model.ReportType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

object BigBrotherReport {

    private lateinit var db: ReportDatabase

    private val job: Job = Job()
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + job)

    internal var nestedLevel = 0

    fun track(type: ReportType, content: String, nestedLevel: Int) = scope.launch {
        _track(type, content, nestedLevel)
    }.let { }

    private suspend fun _track(
        type: ReportType,
        content: String,
        nestedLevel: Int = this.nestedLevel
    ) {
        val entity = ReportLogEntity(
            type = type,
            txtContent = content,
            nestedLevel = nestedLevel
        )
        db.reportLogDao().add(entity)
    }

    fun trackCrash(e: Throwable) = scope.launch {
        db.sessionDao().sessionCrashed(bbSessionId)
        _track(ReportType.CRASH, "X CRASH - ${e.message ?: "without message"}")
    }.let { }

    fun getSessionTimeline(sessionId: Long) = flow {
        val report = db.reportLogDao().getSession(sessionId)
            .buildReport()
            .generate()

        emit(report)
    }.flowOn(Dispatchers.IO)

    fun deleteCurrentSession() = scope.launch {
        Log.d(BBTAG, "Deleting session $bbSessionId")
        db.sessionDao().deleteSession(bbSessionId)
    }

    internal fun listSessions(dateFilter: LocalDate?) = if (dateFilter != null) {
        val startDate = dateFilter.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "T00:00:00"
        val endDate = dateFilter.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "T23:59:59"
        db.sessionDao().getSessionByRange(startDate, endDate)
    } else {
        db.sessionDao().getAllSessions()
    }

    internal fun createSession(context: Context) {
        // Allowed in main thread, to prevent session -1
        db = Room.databaseBuilder(context, ReportDatabase::class.java, "bb-report-db")
            .allowMainThreadQueries()
            .build()

        db.sessionDao().closePreviousSession()
        bbSessionId = db.sessionDao().create()
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