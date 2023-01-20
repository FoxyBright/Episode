package ru.rikmasters.gilty.core.log

import android.util.Log

interface Loggable {
    
    fun <T> anyLog(any: T?, title: String = ""): T? {
        logD("$title$any")
        return any
    }
    
    fun logGroup() = this::class.simpleName ?: "Anonymous"
    
    fun logV(msg: String, thr: Throwable? = null) {
        log.log(msg, logGroup(), Log.VERBOSE)
    }
    
    fun logD(msg: String, thr: Throwable? = null) {
        log.log(msg, logGroup(), Log.DEBUG)
    }
    
    fun logI(msg: String, thr: Throwable? = null) {
        log.log(msg, logGroup(), Log.INFO)
    }
    
    fun logW(msg: String, thr: Throwable? = null) {
        log.log(msg, logGroup(), Log.WARN)
    }
    
    fun logE(msg: String, thr: Throwable? = null) {
        log.log(msg, logGroup(), Log.ERROR)
    }
    
    fun logA(msg: String, thr: Throwable? = null) {
        log.log(msg, logGroup(), Log.ASSERT)
    }
}