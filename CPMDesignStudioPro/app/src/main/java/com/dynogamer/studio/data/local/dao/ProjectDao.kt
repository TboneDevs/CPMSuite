package com.dynogamer.studio.data.local.dao

import androidx.room.*
import com.dynogamer.studio.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Query("SELECT * FROM projects WHERE isArchived = 0 ORDER BY modifiedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE cpmType = :type AND isArchived = 0 ORDER BY modifiedAt DESC")
    fun getProjectsByType(type: String): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Query("SELECT * FROM projects WHERE isFavorite = 1 AND isArchived = 0 ORDER BY modifiedAt DESC")
    fun getFavoriteProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE isArchived = 1 ORDER BY modifiedAt DESC")
    fun getArchivedProjects(): Flow<List<ProjectEntity>>

    @Query("""
        SELECT * FROM projects 
        WHERE (name LIKE '%' || :query || '%' OR vehicleName LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%')
        AND isArchived = 0
        ORDER BY modifiedAt DESC
    """)
    fun searchProjects(query: String): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProject(id: String)

    @Query("UPDATE projects SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE projects SET isArchived = :isArchived WHERE id = :id")
    suspend fun setArchived(id: String, isArchived: Boolean)

    @Query("SELECT COUNT(*) FROM projects WHERE isArchived = 0")
    suspend fun getProjectCount(): Int

    @Query("SELECT COUNT(*) FROM projects WHERE cpmType = :type AND isArchived = 0")
    suspend fun getProjectCountByType(type: String): Int
}
