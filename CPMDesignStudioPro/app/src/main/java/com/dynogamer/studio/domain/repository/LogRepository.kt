package com.dynogamer.studio.domain.repository

import com.dynogamer.studio.domain.model.LogEntry
import kotlinx.coroutines.flow.Flow

interface LogRepository {
    fun getRecentLogs(limit: Int = 500): Flow<List<LogEntry>>
    fun getLogsByLevel(level: String): Flow<List<LogEntry>>
    fun getLogsForProject(projectId: String): Flow<List<LogEntry>>
    fun searchLogs(query: String): Flow<List<LogEntry>>
    suspend fun addLog(level: String, tag: String, message: String, projectId: String? = null)
    suspend fun clearAllLogs()
    suspend fun deleteOldLogs(before: Long)
}
