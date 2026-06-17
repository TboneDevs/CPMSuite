package com.dynogamer.studio.data.repository

import com.dynogamer.studio.data.local.dao.LogDao
import com.dynogamer.studio.data.local.entity.LogEntity
import com.dynogamer.studio.domain.model.LogEntry
import com.dynogamer.studio.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LogRepositoryImpl @Inject constructor(
    private val dao: LogDao
) : LogRepository {

    override fun getRecentLogs(limit: Int): Flow<List<LogEntry>> =
        dao.getRecentLogs(limit).map { list -> list.map { it.toDomain() } }

    override fun getLogsByLevel(level: String): Flow<List<LogEntry>> =
        dao.getLogsByLevel(level).map { list -> list.map { it.toDomain() } }

    override fun getLogsForProject(projectId: String): Flow<List<LogEntry>> =
        dao.getLogsForProject(projectId).map { list -> list.map { it.toDomain() } }

    override fun searchLogs(query: String): Flow<List<LogEntry>> =
        dao.searchLogs(query).map { list -> list.map { it.toDomain() } }

    override suspend fun addLog(level: String, tag: String, message: String, projectId: String?) {
        dao.insertLog(LogEntity(level = level, tag = tag, message = message, projectId = projectId))
    }

    override suspend fun clearAllLogs() = dao.clearAllLogs()

    override suspend fun deleteOldLogs(before: Long) = dao.deleteOldLogs(before)

    private fun LogEntity.toDomain() = LogEntry(
        id = id, level = level, tag = tag, message = message,
        projectId = projectId, timestamp = timestamp
    )
}
