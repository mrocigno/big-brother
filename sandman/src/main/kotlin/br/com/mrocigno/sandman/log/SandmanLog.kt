package br.com.mrocigno.sandman.log

import android.util.Log
import androidx.lifecycle.MutableLiveData
import br.com.mrocigno.sandman.Sandman

class SandmanLog(private val tag: String) {

    fun d(message: String) {
        addEntry(LogEntryType.DEBUG, tag, message)
        if (Sandman.isLoggable) Log.d(tag, message)
    }

    fun d(message: String?, throwable: Throwable?) {
        addEntry(LogEntryType.DEBUG, tag, message, throwable)
        if (Sandman.isLoggable) Log.d(tag, message, throwable)
    }

    fun i(message: String) {
        addEntry(LogEntryType.INFO, tag, message)
        if (Sandman.isLoggable) Log.i(tag, message)
    }

    fun i(message: String?, throwable: Throwable?) {
        addEntry(LogEntryType.INFO, tag, message, throwable)
        if (Sandman.isLoggable) Log.i(tag, message, throwable)
    }

    fun v(message: String) {
        addEntry(LogEntryType.VERBOSE, tag, message)
        if (Sandman.isLoggable) Log.v(tag, message)
    }

    fun v(message: String?, throwable: Throwable?) {
        addEntry(LogEntryType.VERBOSE, tag, message, throwable)
        if (Sandman.isLoggable) Log.v(tag, message, throwable)
    }

    fun e(message: String) {
        addEntry(LogEntryType.ERROR, tag, message)
        if (Sandman.isLoggable) Log.e(tag, message)
    }

    fun e(message: String?, throwable: Throwable?) {
        addEntry(LogEntryType.ERROR, tag, message, throwable)
        if (Sandman.isLoggable) Log.e(tag, message, throwable)
    }

    fun w(message: String) {
        addEntry(LogEntryType.WARN, tag, message)
        if (Sandman.isLoggable) Log.w(tag, message)
    }

    fun w(message: String?, throwable: Throwable?) {
        addEntry(LogEntryType.WARN, tag, message, throwable)
        if (Sandman.isLoggable) Log.w(tag, message, throwable)
    }

    fun w(throwable: Throwable?) {
        addEntry(LogEntryType.WARN, tag, null, throwable)
        if (Sandman.isLoggable) Log.w(tag, throwable)
    }

    companion object {

        private val _logEntries = mutableListOf<LogEntryModel>()
        val logEntries = MutableLiveData<List<LogEntryModel>>()

        fun addEntry(
            lvl: LogEntryType,
            tag: String,
            message: String? = null,
            throwable: Throwable? = null
        ) {
            _logEntries.add(LogEntryModel(lvl, tag, message, throwable))
            logEntries.postValue(_logEntries)
        }

        fun clear() {
            _logEntries.clear()
            logEntries.postValue(emptyList())
        }
    }
}