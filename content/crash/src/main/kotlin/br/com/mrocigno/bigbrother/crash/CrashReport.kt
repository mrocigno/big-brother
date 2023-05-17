package br.com.mrocigno.bigbrother.crash

import br.com.mrocigno.bigbrother.core.model.ReportModel
import br.com.mrocigno.bigbrother.core.model.ReportModelType

class CrashReport(val throwable: Throwable) : ReportModel(ReportModelType.CRASH)