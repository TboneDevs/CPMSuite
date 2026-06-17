package com.dynogamer.studio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logs")
data class LogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val level: String,          // "INFO", "WARN", "ERROR", "DEBUG"
    val tag: String,
    val message: String,
    val projectId: String?,
    val timestamp: Long = System.currentTimeMillis()
)
