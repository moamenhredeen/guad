package app.guad.core.data.logging

import app.guad.core.domain.logging.GuadLogger
import co.touchlab.kermit.Logger

object KermitLogger: GuadLogger {
    override fun info(message: String) {
        Logger.i(message)
    }

    override fun warn(message: String) {
        Logger.w(message)
    }

    override fun error(message: String, throwable: Throwable?) {
        Logger.e(message, throwable)
    }

}