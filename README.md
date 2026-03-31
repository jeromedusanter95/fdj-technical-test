# FDJ Test - Sports League Search App

A modern Android application built with Jetpack Compose that allows users to search for sports leagues and view teams within each league. This project demonstrates Clean Architecture principles, MVVM pattern, and Android best practices.

## 🏗️ Architecture

This project follows **Clean Architecture** with **MVVM (Model-View-ViewModel)** pattern, organized into three main layers:

### Architecture Decision: Single Module

For this small project, I chose a **single-module architecture** to keep things simple and maintainable. However, for larger projects like [Restorik](https://github.com/jerome-dusanter/restorik), I would adopt a **multi-module architecture** with feature modules, core modules, and shared libraries for better:
- Separation of concerns
- Build time optimization
- Team scalability
- Reusability across features

## 📁 Folder Structure Philosophy

### Screen Organization

Each screen follows a consistent pattern:

1. **One Stateful Composable** (`*Screen.kt`)
   - Entry point for the screen
   - Holds ViewModel instance
   - Collects StateFlow and manages lifecycle
   - Delegates UI rendering to stateless components

2. **One ViewModel** (`*ViewModel.kt`)
   - Manages UI state via StateFlow
   - Coordinates business logic through use cases
   - Handles user actions and events
   - Uses Assisted Injection for runtime parameters (TeamsListViewModel)

3. **UI State Data Class** (`*UiState.kt`)
   - Single source of truth for screen state
   - Immutable data class
   - Lives in separate file following "each class in own file" rule

4. **Stateless Components** (`components/`)
   - Pure UI functions with no business logic
   - Receive data via parameters
   - Emit events via callbacks
   - Each component in its own file with `@Preview`

## 🛠️ Tech Stack

### Core
- **Kotlin** - 100% Kotlin codebase
- **Jetpack Compose** - Modern declarative UI
- **Material 3** - Material Design components
- **Coroutines & Flow** - Asynchronous programming
- **StateFlow** - Reactive state management

### Dependency Injection
- **Hilt** - Dependency injection framework
- **Assisted Injection** - Runtime parameter injection for ViewModels

### Networking
- **Retrofit** - HTTP client
- **Moshi** - JSON serialization
- **OkHttp** - Network interceptor and logging

### Navigation
- **Navigation Compose** - Type-safe navigation with kotlinx.serialization
- Custom destinations (`LeagueSearchDestination`, `TeamsListDestination`)

### UI/UX
- **Coil** - Image loading library
- **Lottie** - Animated illustrations for loading, error, and empty states

### Testing
- **JUnit 4** - Testing framework
- **MockK** - Kotlin-friendly mocking library
- **Coroutines Test** - Testing coroutines with `StandardTestDispatcher` and `UnconfinedTestDispatcher`

### Build
- **Gradle 9.1.0** - Build system
- **KSP** - Kotlin Symbol Processing for Hilt

## ✅ Testing Strategy

### Comprehensive Test Coverage

All tests follow the **Given-When-Then** pattern for clarity and consistency.

### Testing Utilities

#### MainDispatcherRule
Custom JUnit `TestWatcher` for automatic dispatcher setup/teardown:

```kotlin
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
```


## 👤 Author

**Jérôme Dusanter**
- GitHub: [@jerome-dusanter](https://github.com/jerome-dusanter)
- Email: dusanter.jerome@gmail.com

---

🤖 Built with assistance from [Claude Code](https://claude.com/claude-code)
