package app.guad.core.domain.logging

interface GuadLogger {
    fun info (message: String)
    fun warn (message: String)
    fun error (message: String, throwable: Throwable? = null)
}