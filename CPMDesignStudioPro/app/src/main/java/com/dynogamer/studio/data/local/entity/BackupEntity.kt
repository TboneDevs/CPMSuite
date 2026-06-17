package com.dynogamer.studio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backups")
data class BackupEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val projectName: String,
    val backupPath: String,
    val triggerEvent: String,   // "import", "edit", "conversion", "export", "manual"
    val sizeBytes: Long,
    val isValid: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
