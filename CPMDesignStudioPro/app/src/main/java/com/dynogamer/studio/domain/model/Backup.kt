package com.dynogamer.studio.domain.model

data class Backup(
    val id: String,
    val projectId: String,
    val projectName: String,
    val backupPath: String,
    val triggerEvent: String,
    val sizeBytes: Long,
    val isValid: Boolean,
    val createdAt: Long
)
