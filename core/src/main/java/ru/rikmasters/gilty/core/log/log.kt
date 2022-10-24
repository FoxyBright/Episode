package ru.rikmasters.gilty.core.log

import android.util.Log

@Suppress("ClassName")
object log {

    const val DEFAULT_GROUP = "default"
    const val DEFAULT_LEVEL = Log.VERBOSE

    private val groups = hashMapOf(DEFAULT_GROUP to DEFAULT_LEVEL)

    private val caller: String
        get() = Thread.currentThread().stackTrace.drop(2).first {
            it.className.substringBeforeLast('.')!=
                    log::class.qualifiedName!!.substringBeforeLast('.')
        }.let { "${it.fileName}:${it.lineNumber}" }

    fun set(group: String, level: Int) {
        groups[group] = level
    }

    fun log(
        msg: String,
        group: String = DEFAULT_GROUP,
        level: Int = DEFAULT_LEVEL,
        thr: Throwable? = null
    ) {
        val minLevel = groups[group]
            ?: DEFAULT_LEVEL.also { groups[group] = it }

        if(level >= minLevel)
            doLog(msg, group, level, thr)
    }

    private fun doLog(msg: String, group: String, level: Int, thr: Throwable?) {
        Log.println(
            level, caller,
            (if(group == DEFAULT_GROUP) msg else "{$group} $msg")
            + Log.getStackTraceString(thr)
        )
    }

    fun v(msg: String, group: String = DEFAULT_GROUP, thr: Throwable? = null) {
        log(msg, group, Log.VERBOSE, thr)
    }

    fun d(msg: String, group: String = DEFAULT_GROUP, thr: Throwable? = null) {
        log(msg, group, Log.DEBUG, thr)
    }

    fun i(msg: String, group: String = DEFAULT_GROUP, thr: Throwable? = null) {
        log(msg, group, Log.INFO, thr)
    }

    fun w(msg: String, group: String = DEFAULT_GROUP, thr: Throwable? = null) {
        log(msg, group, Log.WARN, thr)
    }

    fun e(msg: String, group: String = DEFAULT_GROUP, thr: Throwable? = null) {
        log(msg, group, Log.ERROR, thr)
    }

    fun a(msg: String, group: String = DEFAULT_GROUP, thr: Throwable? = null) {
        log(msg, group, Log.ASSERT, thr)
    }
}