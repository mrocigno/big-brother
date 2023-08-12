package br.com.mrocigno.bigbrother.report

import br.com.mrocigno.bigbrother.common.utils.appendSpace
import br.com.mrocigno.bigbrother.report.entity.ReportLogEntity
import br.com.mrocigno.bigbrother.report.model.ReportType

internal fun List<ReportLogEntity>.buildReport() = ReportGenerator(this)

internal class ReportGenerator(list: List<ReportLogEntity>) {

    private var finalList: List<ReportLogEntity> = list

    // TODO: Add some configs here

    fun generate() = StringBuilder().also {
        it.appendLine("O - Inicio")
        it.appendSpace(1)
        var lastNestedLvl = 0
        for (report in finalList) {
            it.appendLine()
            it.appendSpace(report.nestedLevel)
            it.append(report.txtContent)
            lastNestedLvl = report.nestedLevel
        }
        if (finalList.lastOrNull()?.type != ReportType.CRASH) {
            it.appendLine()
            it.appendSpace(lastNestedLvl)
            it.appendLine()
            it.append("X ${"-".repeat(lastNestedLvl * 4)} Fim")
        }
    }.toString()
}