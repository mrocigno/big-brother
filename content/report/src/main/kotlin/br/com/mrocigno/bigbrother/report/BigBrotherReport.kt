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

object BigBrotherReport {

    private lateinit var db: ReportDatabase

    private val job: Job = Job()
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + job)

    internal var nestedLevel = 0

    fun track(type: ReportType, content: String) = scope.launch {
        _track(type, content)
    }.let { }

    private suspend fun _track(type: ReportType, content: String) {
        db.reportLogDao().add(ReportLogEntity(
            type = type.name,
            txtContent = content,
            nestedLevel = nestedLevel
        ))
    }

    fun trackCrash(e: Throwable) = scope.launch {
        db.sessionDao().sessionCrashed(bbSessionId)
        bbTrack(ReportType.CRASH) {
            "X CRASH - ${e.message ?: "without message"}"
        }
    }.let { }

    fun getSessionTimeline(sessionId: Long) = flow {
        val reports = db.reportLogDao().getSession(sessionId)
        emit(reports.joinToString("\n") {
            it.txtContent
        })
    }.flowOn(Dispatchers.IO)

    fun deleteCurrentSession() = scope.launch {
        Log.d(BBTAG, "Deleting session $bbSessionId")
        db.sessionDao().deleteSession(bbSessionId)
    }

    internal fun createSession(context: Context) {
        db = Room.databaseBuilder(context, ReportDatabase::class.java, "bb-report-db").build()
        scope.launch {
            db.sessionDao().closePreviousSession()
            bbSessionId = db.sessionDao().create()
        }
    }
}

fun bbTrack(type: ReportType, content: () -> String) =
    bbTrack(type, content.invoke())

fun bbTrack(type: ReportType, content: String) =
    BigBrotherReport.track(type, content)