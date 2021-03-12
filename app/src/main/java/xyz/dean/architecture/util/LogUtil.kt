@file:Suppress("unused")

package xyz.dean.architecture.util

import androidx.annotation.IntRange
import xyz.dean.architecture.BuildConfig

/**
 * Log printer for Android.
 */
val alogPrinter = object : LogPrinter() {
    override fun printLog(logType: LogType, tag: String, msg: String, tr: Throwable?) {
        when (logType) {
            LogType.VERBOSE ->  if (tr != null) android.util.Log.v(tag, msg, tr) else android.util.Log.v(tag, msg)
            LogType.DEBUG ->    if (tr != null) android.util.Log.d(tag, msg, tr) else android.util.Log.d(tag, msg)
            LogType.INFO ->     if (tr != null) android.util.Log.i(tag, msg, tr) else android.util.Log.i(tag, msg)
            LogType.WARNING ->  if (tr != null) android.util.Log.w(tag, msg, tr) else android.util.Log.w(tag, msg)
            LogType.ERROR ->    if (tr != null) android.util.Log.e(tag, msg, tr) else android.util.Log.e(tag, msg)
            LogType.ASSERT ->   if (tr != null) android.util.Log.wtf(tag, msg, tr) else android.util.Log.wtf(tag, msg)
        }
    }
}

/**
 * Default global log utils.
 */
val log = Log().apply {
    if (BuildConfig.DEBUG) {
        alogPrinter.setLoggable(LogPrinter.ALL_LOGGABLE)
    } else {
        alogPrinter.setLoggable(LogPrinter.DEFAULT_LOGGABLE)
    }
    printers.add(alogPrinter)
}

class Log {
    var printers: MutableList<LogPrinter> = mutableListOf()
    var tagFilter: (String) -> Boolean = { true }

    fun v(tag: String, tr: Throwable? = null, msg: () -> String)
            = log(LogPrinter.LogType.VERBOSE, tag, msg.invoke(), tr)

    fun v(tag: String, msg: String, tr: Throwable? = null)
            = log(LogPrinter.LogType.VERBOSE, tag, msg, tr)

    fun d(tag: String, tr: Throwable? = null, msg: () -> String)
            = log(LogPrinter.LogType.DEBUG, tag, msg.invoke(), tr)

    fun d(tag: String, msg: String, tr: Throwable? = null)
            = log(LogPrinter.LogType.DEBUG, tag, msg, tr)

    fun i(tag: String, tr: Throwable? = null, msg: () -> String)
            = log(LogPrinter.LogType.INFO, tag, msg.invoke(), tr)

    fun i(tag: String, msg: String, tr: Throwable? = null)
            = log(LogPrinter.LogType.INFO, tag, msg, tr)

    fun w(tag: String, tr: Throwable? = null, msg: () -> String)
            = log(LogPrinter.LogType.WARNING, tag, msg.invoke(), tr)

    fun w(tag: String, msg: String, tr: Throwable? = null)
            = log(LogPrinter.LogType.WARNING, tag, msg, tr)

    fun e(tag: String, tr: Throwable? = null, msg: () -> String)
            = log(LogPrinter.LogType.ERROR, tag, msg.invoke(), tr)

    fun e(tag: String, msg: String, tr: Throwable? = null)
            = log(LogPrinter.LogType.ERROR, tag, msg, tr)

    fun wtf(tag: String, tr: Throwable? = null, msg: () -> String)
            = log(LogPrinter.LogType.ASSERT, tag, msg.invoke(), tr)

    fun wtf(tag: String, msg: String, tr: Throwable? = null)
            = log(LogPrinter.LogType.ASSERT, tag, msg, tr)

    private fun log(type: LogPrinter.LogType, tag: String, msg: String, tr: Throwable? = null) {
        printers.forEach { printer ->
            if (printer.shouldLog(type) && tagFilter.invoke(tag)) {
                printer.printLog(type, tag, msg, tr)
            }
        }
    }
}

abstract class LogPrinter {
    @IntRange(from = 0b0000_0000, to = 0b0011_1111)
    private var loggable: Int = DEFAULT_LOGGABLE

    abstract fun printLog(logType: LogType, tag: String, msg: String, tr: Throwable? = null)

    fun shouldLog(type: LogType) = type.value and loggable != 0

    /**
     * Set what type of log can be printed.
     *
     * @param [types] is a var argument, what LogType you pass in will be allowed to print.
     */
    fun setLoggable(vararg types: LogType) {
        loggable = DISABLE_ALL
        types.forEach {
            loggable = loggable or it.value
        }
    }

    /**
     * Set what type of log can be printed.
     *
     * @param [loggable] is a 6-bit value, and each bit represents a type of Log.
     *
     * ```
     *  LogType    MatchBit
     *  -------------------
     *  Verbose     00_0001
     *  Debug       00_0010
     *  Information 00_0100
     *  Warning     00_1000
     *  Error       01_0000
     *  Assert      10_0000
     *  ```
     */
    fun setLoggable(@IntRange(from = 0b0000_0000, to = 0b0011_1111) loggable: Int) {
        this.loggable = loggable
    }

    enum class LogType(val value: Int) {
        VERBOSE (0b0000_0001),
        DEBUG   (0b0000_0010),
        INFO    (0b0000_0100),
        WARNING (0b0000_1000),
        ERROR   (0b0001_0000),
        ASSERT  (0b0010_0000)
    }

    companion object {
        /** Disable all priorities */
        const val DISABLE_ALL       = 0b0000_0000
        /** priorities > WARNING */
        const val ERROR_LOGGABLE    = 0b0011_0000
        /** priorities > INFO */
        const val DEFAULT_LOGGABLE  = 0b0011_1000
        /** priorities > VERBOSE */
        const val DEBUG_LOGGABLE    = 0b0011_1110
        /** All priorities */
        const val ALL_LOGGABLE      = 0b0011_1111

    }
}