package com.dynogamer.studio.domain.model

data class Es3File(
    val name: String,
    val size: String,
    val lastModified: String,
    val isDirectory: Boolean,
    val isNewest: Boolean = false,
    val fullPath: String = ""
)
