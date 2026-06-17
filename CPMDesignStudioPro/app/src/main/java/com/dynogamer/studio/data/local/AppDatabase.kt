package com.dynogamer.studio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dynogamer.studio.data.local.dao.BackupDao
import com.dynogamer.studio.data.local.dao.LogDao
import com.dynogamer.studio.data.local.dao.ProjectDao
import com.dynogamer.studio.data.local.entity.BackupEntity
import com.dynogamer.studio.data.local.entity.LogEntity
import com.dynogamer.studio.data.local.entity.ProjectEntity

@Database(
    entities = [
        ProjectEntity::class,
        BackupEntity::class,
        LogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun backupDao(): BackupDao
    abstract fun logDao(): LogDao
}
