package br.com.mrocigno.sandman.lucien

import android.content.Context
import android.util.Log
import br.com.mrocigno.sandman.common.utils.appendSpace
import br.com.mrocigno.sandman.core.model.ReportModel
import br.com.mrocigno.sandman.core.model.ReportModelType

fun List<ReportModel>.generateReport() = ReportGenerator(this)

class ReportGenerator(list: List<ReportModel>) {

    private var fileType = "txt"
    private var filterType: ReportModelType? = null
    private var finalList: List<ReportModel> = list

//    fun asTxt() = apply {
//        fileType = "txt"
//    }
//
//    fun asHtml() = apply {
//        fileType = "html"
//    }

    fun filterByType(type: ReportModelType) = apply {
        filterType = type

        finalList = finalList.filter(type)
    }

    private fun List<ReportModel>.filter(type: ReportModelType): List<ReportModel> {
        val tempList = mutableListOf<ReportModel>()
        forEach {
            if (it.type == type) tempList.add(it)
            if (it is ActivityTrack) {
                tempList.addAll(it.reportModels.filter(type))
            }
        }
        return tempList
    }

    fun generate(context: Context) {
        val teste = StringBuilder().also {
            it.appendLine("O - Inicio")
            it.appendSpace(1)
            for (report in finalList) {
                it.appendLine()
                it.append(report.asTxt())
            }
            it.appendLine()
            it.appendSpace(1)
            it.appendLine()
            it.append("X - Fim")
        }.toString()

        Log.d("TOPPER", teste)

        context.openFileOutput("teste.txt", Context.MODE_PRIVATE).bufferedWriter().use {
            it.append(teste)
        }
    }
}