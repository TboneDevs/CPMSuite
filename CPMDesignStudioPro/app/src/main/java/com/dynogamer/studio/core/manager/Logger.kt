package com.dynogamer.studio.core.manager

import com.dynogamer.studio.domain.repository.LogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Logger
 *
 * Writes structured log entries to the Room database and Timber simultaneously.
 */
@Singleton
class Logger @Inject constructor(
    private val logRepository: LogRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun info(tag: String, message: String, projectId: String? = null) {
        Timber.tag(tag).i(message)
        scope.launch { logRepository.addLog("INFO", tag, message, projectId) }
    }

    fun warn(tag: String, message: String, projectId: String? = null) {
        Timber.tag(tag).w(message)
        scope.launch { logRepository.addLog("WARN", tag, message, projectId) }
    }

    fun error(tag: String, message: String, projectId: String? = null) {
        Timber.tag(tag).e(message)
        scope.launch { logRepository.addLog("ERROR", tag, message, projectId) }
    }

    fun debug(tag: String, message: String, projectId: String? = null) {
        Timber.tag(tag).d(message)
        scope.launch { logRepository.addLog("DEBUG", tag, message, projectId) }
    }
}
