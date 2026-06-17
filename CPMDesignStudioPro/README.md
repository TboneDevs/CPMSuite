# CPM Design Transfer Studio Pro

**CPM Design Transfer Studio Pro** is an advanced, production-ready Android tool built for seamless design extraction, preview, conversion, and backup for *Car Parking Multiplayer 1* (CPM1) and *Car Parking Multiplayer 2* (CPM2).

This project is a complete architectural overhaul of the TNNR v5.1 codebase, refactored into a modern Android Studio project utilizing Clean Architecture, MVVM, Jetpack Compose, Room, and Hilt. It fully implements the `CPM_Design_Transfer_Studio_Pro_Master_Spec`.

## Features

- **Dual Workspaces:** Dedicated workspaces for both CPM1 and CPM2.
- **Conversion Studio:** Intelligent conversion of `.es3` save files from CPM1 to CPM2 format.
- **Shizuku Integration:** Rootless access to restricted `/Android/data/` directories on Android 11+.
- **Project Library:** Centralized management of all imported and converted designs with metadata extraction.
- **Preview Studio:** Visual canvas for inspecting vehicle designs, vinyl layers, and structural integrity.
- **Backup Manager:** Automated and manual backup of `.es3` files before destructive operations.
- **Export Center:** Export converted `.es3` files and diagnostic reports.
- **Diagnostics & Logging:** Comprehensive device diagnostics, storage checks, and a real-time log viewer.
- **Modern UI/UX:** A stunning, high-contrast dark red and black interface built entirely in Jetpack Compose.

## Architecture

The app strictly follows **Clean Architecture** principles:

1. **Domain Layer:** Core business logic, models (`Project`, `Backup`, `Es3File`), and repository interfaces.
2. **Data Layer:** Room database (`AppDatabase`, DAOs), file system access, and repository implementations.
3. **Core Layer:** Specialized managers (`ShizukuManager`, `FileManager`, `ImportManager`, `ConversionManager`, `BackupManager`, `DiagnosticsManager`, `Logger`).
4. **UI Layer:** Jetpack Compose screens, ViewModels, and a cohesive design system (`Theme.kt`, `Color.kt`).

Dependency Injection is handled via **Dagger Hilt**.

## Building the Project

### Prerequisites

- **Android Studio:** Jellyfish (2023.3.1) or newer.
- **JDK:** Java 17.
- **Android SDK:** API 34 (Compile SDK).

### Steps

1. Clone or download the project repository.
2. Open the project in Android Studio.
3. Allow Gradle to sync and download all dependencies.
4. Build the project: `Build > Make Project`.
5. Run the app on a physical device or emulator (API 26+).

*Note: Shizuku integration requires a physical device with Shizuku installed and activated, or an emulator configured with root/ADB access.*

## Testing

The project includes unit tests and instrumented UI tests.

- **Unit Tests:** Run via `app/src/test/` (e.g., `ProjectRepositoryImplTest`, `Es3FileUtilsTest`).
- **UI Tests:** Run via `app/src/androidTest/` (e.g., `HomeScreenTest`, `NavigationTest`).

## Dependencies

- **Jetpack Compose:** UI toolkit.
- **Hilt:** Dependency Injection.
- **Room:** Local SQLite database.
- **Coroutines & Flow:** Asynchronous programming and reactive streams.
- **Shizuku API:** System-level file access.
- **Timber:** Logging.

## License

This project is proprietary software developed for internal use based on the TNNR Master Spec.
