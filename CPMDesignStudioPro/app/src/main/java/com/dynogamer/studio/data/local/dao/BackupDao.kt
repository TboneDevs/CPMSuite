package com.dynogamer.studio.data.local.dao

import androidx.room.*
import com.dynogamer.studio.data.local.entity.BackupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BackupDao {

    @Query("SELECT * FROM backups ORDER BY createdAt DESC")
    fun getAllBackups(): Flow<List<BackupEntity>>

    @Query("SELECT * FROM backups WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getBackupsForProject(projectId: String): Flow<List<BackupEntity>>

    @Query("SELECT * FROM backups WHERE id = :id")
    suspend fun getBackupById(id: String): BackupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackup(backup: BackupEntity)

    @Query("DELETE FROM backups WHERE id = :id")
    suspend fun deleteBackup(id: String)

    @Query("DELETE FROM backups WHERE projectId = :projectId")
    suspend fun deleteBackupsForProject(projectId: String)

    @Query("SELECT COUNT(*) FROM backups")
    suspend fun getBackupCount(): Int

    @Query("SELECT SUM(sizeBytes) FROM backups")
    suspend fun getTotalBackupSize(): Long?
}
