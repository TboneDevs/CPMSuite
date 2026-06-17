package com.dynogamer.studio.data.repository

import com.dynogamer.studio.data.local.dao.ProjectDao
import com.dynogamer.studio.data.local.entity.ProjectEntity
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ProjectRepositoryImplTest {

    private lateinit var projectDao: ProjectDao
    private lateinit var repository: ProjectRepositoryImpl

    @Before
    fun setup() {
        projectDao = mockk(relaxed = true)
        repository = ProjectRepositoryImpl(projectDao)
    }

    @Test
    fun `getAllProjects returns mapped domain models`() = runTest {
        val entity = ProjectEntity(
            id = "test-id",
            name = "Test Project",
            vehicleName = "Lamborghini",
            cpmType = "CPM1",
            status = "Ready",
            workingCopyPath = "/data/test.es3",
            originalFilePath = "/data/test.es3",
            isFavorite = false,
            isArchived = false,
            createdAt = System.currentTimeMillis(),
            modifiedAt = System.currentTimeMillis()
        )
        every { projectDao.getAllProjects() } returns flowOf(listOf(entity))

        repository.getAllProjects().collect { projects ->
            assertEquals(1, projects.size)
            assertEquals("test-id", projects[0].id)
            assertEquals("Test Project", projects[0].name)
            assertEquals("CPM1", projects[0].cpmType)
        }
    }

    @Test
    fun `getProjectsByType filters correctly`() = runTest {
        val cpm1Entity = ProjectEntity(
            id = "cpm1-id", name = "CPM1 Project", vehicleName = "BMW",
            cpmType = "CPM1", status = "Ready", workingCopyPath = "", originalFilePath = "",
            isFavorite = false, isArchived = false,
            createdAt = 0L, modifiedAt = 0L
        )
        every { projectDao.getProjectsByType("CPM1") } returns flowOf(listOf(cpm1Entity))

        repository.getProjectsByType("CPM1").collect { projects ->
            assertEquals(1, projects.size)
            assertEquals("CPM1", projects[0].cpmType)
        }
    }

    @Test
    fun `saveProject calls dao insert`() = runTest {
        val entity = ProjectEntity(
            id = "save-id", name = "Save Test", vehicleName = "Ferrari",
            cpmType = "CPM2", status = "Ready", workingCopyPath = "", originalFilePath = "",
            isFavorite = false, isArchived = false,
            createdAt = 0L, modifiedAt = 0L
        )
        coEvery { projectDao.insertProject(any()) } just Runs

        repository.saveProject(
            com.dynogamer.studio.domain.model.Project(
                id = "save-id", name = "Save Test", vehicleName = "Ferrari",
                cpmType = "CPM2", status = "Ready", workingCopyPath = "", originalFilePath = "",
                isFavorite = false, isArchived = false,
                createdAt = 0L, modifiedAt = 0L
            )
        )

        coVerify { projectDao.insertProject(any()) }
    }

    @Test
    fun `deleteProject calls dao delete`() = runTest {
        coEvery { projectDao.deleteProjectById(any()) } just Runs

        repository.deleteProject("del-id")

        coVerify { projectDao.deleteProjectById("del-id") }
    }

    @Test
    fun `setFavorite calls dao update`() = runTest {
        coEvery { projectDao.setFavorite(any(), any()) } just Runs

        repository.setFavorite("fav-id", true)

        coVerify { projectDao.setFavorite("fav-id", true) }
    }
}
