package br.com.mrocigno.bigbrother.log

import android.util.Log
import br.com.mrocigno.bigbrother.log.BBLog.Companion.DEFAULT_TAG
import timber.log.Timber

class BigBrotherLogTree : Timber.Tree() {

    init {
        BBLog.isLoggable = false
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val rawMessage = t?.let {
            message.replace("\n${it.stackTraceToString()}", "")
        } ?: message

        when (priority) {
            Log.DEBUG -> BBLog.tag(tag ?: DEFAULT_TAG).d(rawMessage, t)
            Log.INFO -> BBLog.tag(tag ?: DEFAULT_TAG).i(rawMessage, t)
            Log.WARN -> BBLog.tag(tag ?: DEFAULT_TAG).w(rawMessage, t)
            Log.ERROR -> BBLog.tag(tag ?: DEFAULT_TAG).e(rawMessage, t)
            Log.ASSERT -> BBLog.tag(tag ?: DEFAULT_TAG).wtf(rawMessage, t)
            Log.VERBOSE -> BBLog.tag(tag ?: DEFAULT_TAG).v(rawMessage, t)
        }
    }
}