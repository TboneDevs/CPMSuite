package com.dynogamer.studio.domain.model

data class Project(
    val id: String,
    val name: String,
    val vehicleName: String,
    val cpmType: String,
    val originalFilePath: String,
    val workingCopyPath: String,
    val backupPath: String?,
    val notes: String?,
    val tags: List<String>,
    val category: String?,
    val isFavorite: Boolean,
    val isArchived: Boolean,
    val status: String,
    val createdAt: Long,
    val modifiedAt: Long
)
