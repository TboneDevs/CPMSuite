package com.dynogamer.studio.ui.screens.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dynogamer.studio.core.manager.ProjectManager
import com.dynogamer.studio.domain.model.Project
import com.dynogamer.studio.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProjectsUiState(
    val projects: List<Project> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val projectManager: ProjectManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectsUiState())
    val uiState: StateFlow<ProjectsUiState> = _uiState.asStateFlow()

    private var currentFilter = "All"
    private var currentQuery = ""

    init { loadProjects() }

    private fun loadProjects() {
        viewModelScope.launch {
            getProjectFlow().collect { projects ->
                _uiState.update { it.copy(projects = projects, isLoading = false) }
            }
        }
    }

    private fun getProjectFlow(): Flow<List<Project>> = when (currentFilter) {
        "CPM1" -> projectRepository.getProjectsByType("CPM1")
        "CPM2" -> projectRepository.getProjectsByType("CPM2")
        "Favorites" -> projectRepository.getFavoriteProjects()
        "Archived" -> projectRepository.getArchivedProjects()
        else -> if (currentQuery.isNotEmpty()) projectRepository.searchProjects(currentQuery)
                else projectRepository.getAllProjects()
    }

    fun search(query: String) {
        currentQuery = query
        loadProjects()
    }

    fun setFilter(filter: String) {
        currentFilter = filter
        loadProjects()
    }

    fun toggleFavorite(project: Project) {
        viewModelScope.launch { projectRepository.setFavorite(project.id, !project.isFavorite) }
    }

    fun toggleArchive(project: Project) {
        viewModelScope.launch { projectRepository.setArchived(project.id, !project.isArchived) }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch { projectManager.deleteProject(project) }
    }

    fun duplicateProject(project: Project) {
        viewModelScope.launch { projectManager.duplicateProject(project) }
    }
}
