package com.dynogamer.studio.domain.repository

import com.dynogamer.studio.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getAllProjects(): Flow<List<Project>>
    fun getProjectsByType(type: String): Flow<List<Project>>
    fun getFavoriteProjects(): Flow<List<Project>>
    fun getArchivedProjects(): Flow<List<Project>>
    fun searchProjects(query: String): Flow<List<Project>>
    suspend fun getProjectById(id: String): Project?
    suspend fun saveProject(project: Project)
    suspend fun deleteProject(id: String)
    suspend fun setFavorite(id: String, isFavorite: Boolean)
    suspend fun setArchived(id: String, isArchived: Boolean)
    suspend fun getProjectCount(): Int
}
