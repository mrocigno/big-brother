package br.com.mrocigno.bigbrother.session.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import br.com.mrocigno.bigbrother.core.model.ReportModel
import br.com.mrocigno.bigbrother.report.ActivityReport
import br.com.mrocigno.bigbrother.session.entity.ReportLogEntity

@Dao
internal interface ReportLogDao {

    @Insert(onConflict = REPLACE)
    suspend fun addAll(list: List<ReportLogEntity>): Array<Long>
}

internal suspend fun ReportLogDao.aaadsada(list: MutableList<ReportModel>) {
    addAll(list.aaa())
}

private fun List<ReportModel>.aaa(lastId: Long = -1): List<ReportLogEntity> {
    val result = mutableListOf<ReportLogEntity>()

    var id = lastId
    forEach{ model ->
        result.add(ReportLogEntity(
            id = ++id,
            type = model.type.name,
            txtContent = model.asTxt(),
            htmlContent = "",
            time = model.time
        ))

        if (model is ActivityReport) {
            val aaa = model.reportModels.aaa(id)
            id += aaa.lastIndex

            result.addAll(aaa)
        }
    }

    return result
}