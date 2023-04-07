package br.com.mrocigno.bigbrother.log

import android.util.Log
import androidx.lifecycle.MutableLiveData
import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.utils.track

class BBLog(private val tag: String) {

    fun d(message: String) {
        addEntry(LogEntryType.DEBUG, tag, message)
        if (isLoggable) Log.d(tag, message)
    }

    fun d(message: String?, throwable: Throwable?) {
        addEntry(LogEntryType.DEBUG, tag, message, throwable)
        if (isLoggable) Log.d(tag, message, throwable)
    }

    fun i(message: String) {
        addEntry(LogEntryType.INFO, tag, message)
        if (isLoggable) Log.i(tag, message)
    }

    fun i(message: String?, throwable: Throwable?) {
        addEntry(LogEntryType.INFO, tag, message, throwable)
        if (isLoggable) Log.i(tag, message, throwable)
    }

    fun v(message: String) {
        addEntry(LogEntryType.VERBOSE, tag, message)
        if (isLoggable) Log.v(tag, message)
    }

    fun v(message: String?, throwable: Throwable?) {
        addEntry(LogEntryType.VERBOSE, tag, message, throwable)
        if (isLoggable) Log.v(tag, message, throwable)
    }

    fun e(message: String) {
        addEntry(LogEntryType.ERROR, tag, message)
        if (isLoggable) Log.e(tag, message)
    }

    fun e(message: String?, throwable: Throwable?) {
        addEntry(LogEntryType.ERROR, tag, message, throwable)
        if (isLoggable) Log.e(tag, message, throwable)
    }

    fun w(message: String) {
        addEntry(LogEntryType.WARN, tag, message)
        if (isLoggable) Log.w(tag, message)
    }

    fun w(message: String?, throwable: Throwable?) {
        addEntry(LogEntryType.WARN, tag, message, throwable)
        if (isLoggable) Log.w(tag, message, throwable)
    }

    fun w(throwable: Throwable?) {
        addEntry(LogEntryType.WARN, tag, null, throwable)
        if (isLoggable) Log.w(tag, throwable)
    }

    companion object {

        private val _logEntries = mutableListOf<LogEntryModel>()
        val logEntries = MutableLiveData<List<LogEntryModel>>()
        var isLoggable = true

        fun addEntry(
            lvl: LogEntryType,
            tag: String,
            message: String? = null,
            throwable: Throwable? = null
        ) {
            val model = LogEntryModel(lvl, tag, message, throwable)
            model.track()
            _logEntries.add(model)
            logEntries.postValue(_logEntries)
        }

        fun clear() {
            _logEntries.clear()
            logEntries.postValue(emptyList())
        }

        fun BigBrother.tag(tag: String = "BIG BROTHER") = BBLog(tag)

        fun tag(tag: String = "MINITRUE") = BBLog(tag)
    }
}