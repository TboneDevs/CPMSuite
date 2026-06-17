# Architecture & Technical Documentation

## Overview

CPM Design Transfer Studio Pro is built using a modern Android tech stack, adhering to **Clean Architecture** and the **MVVM (Model-View-ViewModel)** pattern. The goal is to separate concerns, making the app scalable, testable, and maintainable.

## Layer Breakdown

### 1. Domain Layer (`com.dynogamer.studio.domain`)
The innermost layer. It contains the core business rules and has **no dependencies** on Android framework classes (except for basic annotations).
- **Models:** Pure Kotlin data classes representing the core entities (`Project`, `Backup`, `LogEntry`, `Es3File`).
- **Repository Interfaces:** Contracts defining data operations (`ProjectRepository`, `BackupRepository`, `LogRepository`). The domain layer dictates *what* data it needs, not *how* it is fetched.

### 2. Data Layer (`com.dynogamer.studio.data`)
Responsible for data retrieval and persistence. It implements the interfaces defined in the Domain Layer.
- **Local Data Source (Room):** 
  - `AppDatabase`: The main Room database class.
  - `Entities`: Database tables mapping to domain models (`ProjectEntity`, `BackupEntity`, `LogEntity`).
  - `DAOs`: Data Access Objects for querying the database.
- **Repository Implementations:** Classes that fetch data from Room and map `Entities` back to Domain `Models`.

### 3. Core Layer (`com.dynogamer.studio.core`)
Contains essential utility classes and managers that handle specific, complex tasks.
- **`ShizukuManager`:** Interfaces with the Shizuku API to perform file operations in restricted directories (`/Android/data/`).
- **`FileManager`:** General file system operations, I/O, and path resolution.
- **`Es3FileUtils`:** Utility functions specifically for parsing and handling `.es3` files.
- **`ImportManager` / `ExportManager`:** Orchestrates the movement of files into and out of the app's internal storage.
- **`BackupManager`:** Handles the creation, validation, and restoration of project backups.
- **`CPMScanner`:** Scans the device for installed CPM apps and verifies path accessibility.
- **`Logger`:** A centralized logging mechanism that writes to both Logcat (via Timber) and the local Room database for user-facing logs.

### 4. UI Layer (`com.dynogamer.studio.ui`)
The outermost layer, built entirely with **Jetpack Compose**.
- **Screens:** Composable functions representing individual views (`HomeScreen`, `Cpm1Screen`, `ConversionScreen`, etc.).
- **Components:** Reusable UI elements (`StudioCard`, `AccentButton`, `StatusChip`).
- **ViewModels:** Manage UI state and handle user interactions. They communicate with the Domain and Core layers. ViewModels expose state via `StateFlow`.
- **Theme:** Defines the design system (Colors, Typography, Shapes).

## Dependency Injection

**Dagger Hilt** is used to wire everything together.
- `DatabaseModule`: Provides the Room database and DAOs.
- `RepositoryModule`: Binds the repository implementations to their interfaces.
- `ManagerModule` (Implicit via `@Inject constructor`): Provides the core managers.

## Data Flow Example: Importing a Project

1. **User Action:** User taps "Import File" on the `Cpm1Screen`.
2. **ViewModel:** `Cpm1ViewModel.importFile(uri)` is called.
3. **Core Manager:** ViewModel delegates to `ImportManager.importFile()`.
4. **File Operations:** `ImportManager` uses `FileManager` (or `ShizukuManager` if applicable) to copy the `.es3` file to the app's internal `projects` directory.
5. **Database Operation:** `ImportManager` creates a `Project` domain model and calls `ProjectRepository.saveProject()`.
6. **Persistence:** `ProjectRepositoryImpl` maps the model to a `ProjectEntity` and saves it via `ProjectDao`.
7. **UI Update:** The `ProjectRepository`'s `Flow` emits the new list of projects. `Cpm1ViewModel` observes this flow and updates its `uiState`. Compose automatically recomposes the screen to show the new project.

## Error Handling & Logging

All significant actions (imports, conversions, exports) are wrapped in try-catch blocks within the Core Managers. Errors are logged using the custom `Logger` class, which saves them to the database. The UI layer observes these errors (often returned as sealed class `Result` types) and displays appropriate feedback (e.g., Snackbars or status text).
