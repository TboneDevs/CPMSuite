package com.dynogamer.studio.data.local.dao

import androidx.room.*
import com.dynogamer.studio.data.local.entity.LogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {

    @Query("SELECT * FROM logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 500): Flow<List<LogEntity>>

    @Query("SELECT * FROM logs WHERE level = :level ORDER BY timestamp DESC")
    fun getLogsByLevel(level: String): Flow<List<LogEntity>>

    @Query("SELECT * FROM logs WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getLogsForProject(projectId: String): Flow<List<LogEntity>>

    @Query("""
        SELECT * FROM logs 
        WHERE message LIKE '%' || :query || '%' OR tag LIKE '%' || :query || '%'
        ORDER BY timestamp DESC
    """)
    fun searchLogs(query: String): Flow<List<LogEntity>>

    @Insert
    suspend fun insertLog(log: LogEntity)

    @Query("DELETE FROM logs WHERE timestamp < :before")
    suspend fun deleteOldLogs(before: Long)

    @Query("DELETE FROM logs")
    suspend fun clearAllLogs()

    @Query("SELECT COUNT(*) FROM logs")
    suspend fun getLogCount(): Int
}
