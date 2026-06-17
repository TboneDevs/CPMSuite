package com.dynogamer.studio.core.manager

import com.dynogamer.studio.domain.model.Backup
import com.dynogamer.studio.domain.model.Project
import com.dynogamer.studio.domain.repository.BackupRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File

class BackupManagerTest {

    private lateinit var backupRepository: BackupRepository
    private lateinit var logger: Logger
    private lateinit var backupManager: BackupManager

    private val testProject = Project(
        id = "proj-1",
        name = "Test Project",
        vehicleName = "Ferrari",
        cpmType = "CPM2",
        status = "Ready",
        workingCopyPath = "/tmp/test_slot1.es3",
        originalFilePath = "/tmp/test_slot1.es3",
        isFavorite = false,
        isArchived = false,
        createdAt = System.currentTimeMillis(),
        modifiedAt = System.currentTimeMillis()
    )

    @Before
    fun setup() {
        backupRepository = mockk(relaxed = true)
        logger = mockk(relaxed = true)
        // BackupManager needs a context for file dirs; use a temp dir for tests
        backupManager = BackupManager(
            backupRepository = backupRepository,
            logger = logger,
            backupBaseDir = File(System.getProperty("java.io.tmpdir"), "cpm_test_backups")
        )
    }

    @Test
    fun `validateBackup returns false for non-existent file`() = runTest {
        val backup = Backup(
            id = "b1",
            projectId = "proj-1",
            projectName = "Test",
            backupFilePath = "/nonexistent/path/backup.es3",
            triggerEvent = "manual",
            sizeBytes = 0L,
            isValid = false,
            createdAt = System.currentTimeMillis()
        )
        val result = backupManager.validateBackup(backup)
        assertFalse(result)
    }

    @Test
    fun `createBackup creates backup entry`() = runTest {
        // Create a temp source file
        val tempFile = File(System.getProperty("java.io.tmpdir"), "test_slot1.es3")
        tempFile.writeText("fake es3 content for testing")

        val projectWithRealFile = testProject.copy(workingCopyPath = tempFile.absolutePath)
        coEvery { backupRepository.saveBackup(any()) } just Runs

        val backup = backupManager.createBackup(projectWithRealFile, "test")

        assertNotNull(backup)
        assertEquals("proj-1", backup?.projectId)
        assertEquals("test", backup?.triggerEvent)

        tempFile.delete()
    }

    @Test
    fun `deleteBackup removes backup from repository`() = runTest {
        val backup = Backup(
            id = "b-del",
            projectId = "proj-1",
            projectName = "Test",
            backupFilePath = "/tmp/fake.es3",
            triggerEvent = "manual",
            sizeBytes = 100L,
            isValid = true,
            createdAt = System.currentTimeMillis()
        )
        coEvery { backupRepository.deleteBackup(any()) } just Runs

        backupManager.deleteBackup(backup)

        coVerify { backupRepository.deleteBackup(backup.id) }
    }
}
