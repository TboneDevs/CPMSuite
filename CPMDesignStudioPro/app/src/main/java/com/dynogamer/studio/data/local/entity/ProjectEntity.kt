package com.dynogamer.studio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val vehicleName: String,
    val cpmType: String,           // "CPM1" or "CPM2"
    val originalFilePath: String,
    val workingCopyPath: String,
    val backupPath: String?,
    val previewData: String?,      // JSON blob
    val exportData: String?,       // JSON blob
    val notes: String?,
    val tags: String?,             // comma-separated
    val category: String?,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val status: String = "active", // active, archived, deleted
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis(),
    val importHistory: String?,    // JSON array
    val backupHistory: String?,    // JSON array
    val exportHistory: String?     // JSON array
)
