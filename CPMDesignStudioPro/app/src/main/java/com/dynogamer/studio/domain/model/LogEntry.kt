package com.dynogamer.studio.domain.model

data class LogEntry(
    val id: Long,
    val level: String,
    val tag: String,
    val message: String,
    val projectId: String?,
    val timestamp: Long
)
