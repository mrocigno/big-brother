package br.com.mrocigno.sandman.lucien.crash

import br.com.mrocigno.sandman.core.model.ReportModel
import br.com.mrocigno.sandman.core.model.ReportModelType

class CrashReport(val throwable: Throwable) : ReportModel(ReportModelType.CRASH)