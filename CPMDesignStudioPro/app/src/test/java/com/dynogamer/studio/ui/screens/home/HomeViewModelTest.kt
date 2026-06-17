package com.dynogamer.studio.ui.screens.home

import com.dynogamer.studio.core.manager.CPMScanner
import com.dynogamer.studio.core.manager.ShizukuManager
import com.dynogamer.studio.domain.model.Backup
import com.dynogamer.studio.domain.model.Project
import com.dynogamer.studio.domain.repository.BackupRepository
import com.dynogamer.studio.domain.repository.ProjectRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var projectRepository: ProjectRepository
    private lateinit var backupRepository: BackupRepository
    private lateinit var cpmScanner: CPMScanner
    private lateinit var shizukuManager: ShizukuManager
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        projectRepository = mockk(relaxed = true)
        backupRepository = mockk(relaxed = true)
        cpmScanner = mockk(relaxed = true)
        shizukuManager = mockk(relaxed = true)

        every { projectRepository.getProjectsByType("CPM1") } returns flowOf(emptyList())
        every { projectRepository.getProjectsByType("CPM2") } returns flowOf(emptyList())
        every { backupRepository.getAllBackups() } returns flowOf(emptyList())
        every { shizukuManager.isAuthorized() } returns false

        viewModel = HomeViewModel(projectRepository, backupRepository, cpmScanner, shizukuManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has zero counts`() = runTest {
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals(0, state.cpm1Count)
        assertEquals(0, state.cpm2Count)
        assertEquals(0, state.backupCount)
    }

    @Test
    fun `initial state reflects shizuku authorization`() = runTest {
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.shizukuAuthorized)
    }

    @Test
    fun `scanDevice updates scan result`() = runTest {
        val mockResult = CPMScanner.ScanResult(
            cpm1Installed = true, cpm2Installed = false,
            cpm1Es3PathAccessible = true, cpm2Es3PathAccessible = false,
            shizukuAvailable = true, shizukuAuthorized = true,
            issues = emptyList()
        )
        coEvery { cpmScanner.scanDevice() } returns mockResult

        viewModel.scanDevice()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.scanResult)
        assertTrue(state.scanResult!!.cpm1Installed)
        assertTrue(state.shizukuAuthorized)
    }

    @Test
    fun `project counts update when projects are added`() = runTest {
        val cpm1Projects = listOf(
            Project("1", "P1", "Car", "CPM1", "Ready", "", "", false, false, 0L, 0L),
            Project("2", "P2", "Car2", "CPM1", "Ready", "", "", false, false, 0L, 0L)
        )
        every { projectRepository.getProjectsByType("CPM1") } returns flowOf(cpm1Projects)

        viewModel = HomeViewModel(projectRepository, backupRepository, cpmScanner, shizukuManager)
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.cpm1Count)
    }
}
