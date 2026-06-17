package com.dynogamer.studio.data.repository

import com.dynogamer.studio.data.local.dao.ProjectDao
import com.dynogamer.studio.data.local.entity.ProjectEntity
import com.dynogamer.studio.domain.model.Project
import com.dynogamer.studio.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    private val dao: ProjectDao
) : ProjectRepository {

    override fun getAllProjects(): Flow<List<Project>> =
        dao.getAllProjects().map { list -> list.map { it.toDomain() } }

    override fun getProjectsByType(type: String): Flow<List<Project>> =
        dao.getProjectsByType(type).map { list -> list.map { it.toDomain() } }

    override fun getFavoriteProjects(): Flow<List<Project>> =
        dao.getFavoriteProjects().map { list -> list.map { it.toDomain() } }

    override fun getArchivedProjects(): Flow<List<Project>> =
        dao.getArchivedProjects().map { list -> list.map { it.toDomain() } }

    override fun searchProjects(query: String): Flow<List<Project>> =
        dao.searchProjects(query).map { list -> list.map { it.toDomain() } }

    override suspend fun getProjectById(id: String): Project? =
        dao.getProjectById(id)?.toDomain()

    override suspend fun saveProject(project: Project) =
        dao.insertProject(project.toEntity())

    override suspend fun deleteProject(id: String) =
        dao.deleteProject(id)

    override suspend fun setFavorite(id: String, isFavorite: Boolean) =
        dao.setFavorite(id, isFavorite)

    override suspend fun setArchived(id: String, isArchived: Boolean) =
        dao.setArchived(id, isArchived)

    override suspend fun getProjectCount(): Int =
        dao.getProjectCount()

    // ---- Mappers ----

    private fun ProjectEntity.toDomain() = Project(
        id = id,
        name = name,
        vehicleName = vehicleName,
        cpmType = cpmType,
        originalFilePath = originalFilePath,
        workingCopyPath = workingCopyPath,
        backupPath = backupPath,
        notes = notes,
        tags = tags?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList(),
        category = category,
        isFavorite = isFavorite,
        isArchived = isArchived,
        status = status,
        createdAt = createdAt,
        modifiedAt = modifiedAt
    )

    private fun Project.toEntity() = ProjectEntity(
        id = id,
        name = name,
        vehicleName = vehicleName,
        cpmType = cpmType,
        originalFilePath = originalFilePath,
        workingCopyPath = workingCopyPath,
        backupPath = backupPath,
        previewData = null,
        exportData = null,
        notes = notes,
        tags = tags.joinToString(","),
        category = category,
        isFavorite = isFavorite,
        isArchived = isArchived,
        status = status,
        createdAt = createdAt,
        modifiedAt = modifiedAt,
        importHistory = null,
        backupHistory = null,
        exportHistory = null
    )
}
