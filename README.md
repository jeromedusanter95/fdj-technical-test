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

### Layer Structure

```
app/
├── data/           # Data layer
│   ├── api/        # Retrofit API services
│   ├── model/      # DTOs (Data Transfer Objects)
│   └── repository/ # Repository implementations
├── domain/         # Domain layer
│   ├── model/      # Domain models
│   ├── repository/ # Repository interfaces
│   └── usecase/    # Business logic use cases
└── ui/             # Presentation layer
    ├── components/ # Reusable stateless UI components
    ├── navigation/ # Navigation setup and destinations
    ├── screen/     # Feature screens
    │   ├── leaguesearch/
    │   │   ├── components/        # Screen-specific stateless components
    │   │   ├── LeagueSearchScreen.kt     # Stateful composable
    │   │   ├── LeagueSearchViewModel.kt  # ViewModel
    │   │   └── LeagueSearchUiState.kt    # UI state data class
    │   └── teamslist/
    │       ├── components/        # Screen-specific stateless components
    │       ├── TeamsListScreen.kt        # Stateful composable
    │       ├── TeamsListViewModel.kt     # ViewModel with Assisted Injection
    │       └── TeamsListUiState.kt       # UI state data class
    └── theme/      # Material Design theme
```

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

### Example: LeagueSearch Screen

```kotlin
// LeagueSearchScreen.kt - Stateful
@Composable
fun LeagueSearchScreen(
    onLeagueSelected: (String) -> Unit,
    viewModel: LeagueSearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LeagueSearchContent(  // Delegates to stateless component
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onLeagueClick = { league ->
            viewModel.onLeagueSelected(league)
            onLeagueSelected(league.name)
        },
        onRetry = viewModel::retry
    )
}

// components/LeagueSearchContent.kt - Stateless
@Composable
fun LeagueSearchContent(
    uiState: LeagueSearchUiState,
    onSearchQueryChange: (String) -> Unit,
    onLeagueClick: (League) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pure UI rendering with no business logic
}
```

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
- **Turbine** - Flow testing library
- **Coroutines Test** - Testing coroutines with `StandardTestDispatcher` and `UnconfinedTestDispatcher`

### Build
- **Gradle 9.1.0** - Build system
- **KSP** - Kotlin Symbol Processing for Hilt

## ✅ Testing Strategy

### Comprehensive Test Coverage

All tests follow the **Given-When-Then** pattern for clarity and consistency.

#### Test Organization

```
test/
├── domain/usecase/
│   ├── GetAllLeaguesUseCaseTest.kt      (3 tests)
│   ├── GetTeamsByLeagueUseCaseTest.kt   (6 tests)
│   └── SearchLeaguesUseCaseTest.kt      (6 tests)
├── data/repository/
│   └── SportsRepositoryImplTest.kt      (9 tests)
└── ui/screen/
    ├── leaguesearch/
    │   └── LeagueSearchViewModelTest.kt (11 tests)
    └── teamslist/
        └── TeamsListViewModelTest.kt    (6 tests)
```

**Total: 41 unit tests - 100% passing**

### Test Patterns

#### Use Case Tests
```kotlin
@Test
fun `invoke should filter leagues by query case-insensitively`() = runTest {
    // Given
    val leagues = listOf(
        League("1", "English Premier League", "Soccer"),
        League("2", "Spanish La Liga", "Soccer")
    )
    coEvery { repository.getAllLeagues() } returns Result.success(leagues)

    // When
    val result = useCase.invoke("premier")

    // Then
    assertTrue(result.isSuccess)
    assertEquals(1, result.getOrNull()?.size)
    assertEquals("English Premier League", result.getOrNull()?.first()?.name)
}
```

#### Repository Tests
```kotlin
@Test
fun `getAllLeagues should convert null fields to empty strings`() = runTest {
    // Given
    val leagueDtos = listOf(
        LeagueDto(id = null, name = "League", sport = "Soccer")
    )
    coEvery { apiService.getAllLeagues() } returns LeaguesResponse(leagueDtos)

    // When
    val result = repository.getAllLeagues()

    // Then
    assertTrue(result.isSuccess)
    assertEquals("", result.getOrNull()?.first()?.id)
}
```

#### ViewModel Tests with Assisted Injection
```kotlin
private fun createViewModel(leagueName: String): TeamsListViewModel {
    return TeamsListViewModel(
        getTeamsByLeagueUseCase = getTeamsByLeagueUseCase,
        leagueName = leagueName  // Direct parameter injection!
    )
}

@Test
fun `init should set league name and load teams successfully`() = runTest(testDispatcher) {
    // Given
    val leagueName = "French Ligue 1"
    val teams = listOf(...)
    coEvery { getTeamsByLeagueUseCase(leagueName) } returns Result.success(teams)

    // When
    viewModel = createViewModel(leagueName)
    advanceUntilIdle()

    // Then
    val state = viewModel.uiState.value
    assertEquals(leagueName, state.leagueName)
    assertEquals(teams, state.teams)
}
```

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

#### Dispatcher Selection
- **UnconfinedTestDispatcher**: Immediate execution for simple async tests (repository, use cases)
- **StandardTestDispatcher**: Controlled execution for testing delays and timing (ViewModel with debouncing)

### Test Coverage

- ✅ **Domain Layer**: 100% use case coverage
- ✅ **Data Layer**: Repository implementation with null handling, error cases
- ✅ **Presentation Layer**: ViewModel state management, user interactions, debouncing

## 🎨 Key Features

### 1. League Search
- Search leagues by name with debounced input (300ms)
- Display filtered results in real-time
- Show "No results" state for empty searches
- Error handling with retry mechanism

### 2. Teams List
- Grid layout (2 columns) for team display
- Team badges loaded with Coil
- Loading, error, and empty states
- Back navigation to league search

### 3. UI Components

#### Reusable Stateless Components
- `LoadingIndicator` - Circular progress indicator
- `ErrorState` - Lottie error animation with retry button
- `EmptyState` - Lottie empty state animation
- `SearchTextField` - Custom search input with clear button

#### Screen-Specific Components
- `LeagueSearchItem` - League card with name and sport
- `TeamCard` - Team card with badge and name

## 🔧 Code Quality Standards

### Kotlin Best Practices
- ❌ **No `!!` (non-null assertion)** - Always use safe alternatives (`?.let`, `?:`, `requireNotNull`)
- ❌ **No `[]` array access** - Use `.getOrNull()` for safe access
- ✅ **Algorithm optimization** - O(1) lookups with `associateBy` instead of O(n²) with `find` in loops
- ✅ **Prefer `mapNotNull`** over `filterNotNull().map()` to avoid extra iteration

### Architecture Principles
- **SOLID principles** throughout the codebase
- **Dependency Inversion**: ViewModels depend on repository interfaces, not implementations
- **Single Responsibility**: Each class has one reason to change
- **Repository Pattern**: All repositories are interfaces with implementations
  - Interfaces: `domain/repository/`
  - Implementations: `data/repository/*Impl.kt`

### UI Patterns
- **Stateless Components**: Every stateless composable lives in its own file with `@Preview`
- **Assisted Injection**: Runtime parameters for ViewModels following NowInAndroid pattern
- **String Externalization**: All UI strings in `strings.xml` for localization support
- **Centered TopAppBar Titles**: Consistent UI alignment across screens

### Navigation
- **Type-safe Navigation**: Using kotlinx.serialization for route parameters
- **Destination Classes**: Separate navigation destinations from screen composables
  - `LeagueSearchDestination` (navigation data class)
  - `LeagueSearchScreen` (UI composable function)

## 🚀 Getting Started

### Prerequisites
- Android Studio Koala or later
- JDK 17 or later
- Android SDK 34

### Build & Run

```bash
# Clone the repository
git clone https://github.com/yourusername/FDJTest.git

# Open in Android Studio
# Sync Gradle dependencies
# Run on emulator or device
```

### Run Tests

```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.jeromedusanter.fdjtest.domain.usecase.SearchLeaguesUseCaseTest"

# Generate test report
# Open: app/build/reports/tests/testDebugUnitTest/index.html
```

## 📝 API

This project uses [TheSportsDB API](https://www.thesportsdb.com/api.php) for sports data:
- `GET /all_leagues.php` - Fetch all leagues
- `GET /search_all_teams.php?l={league}` - Search teams by league name

## 🎯 Design Decisions

### Why Assisted Injection for ViewModels?

Following the **NowInAndroid** pattern for cleaner tests:

**Without Assisted Injection:**
```kotlin
val savedStateHandle = SavedStateHandle().apply {
    set("LEAGUE_NAME_KEY", "French Ligue 1")  // Magic strings, boilerplate
}
viewModel = TeamsListViewModel(savedStateHandle, useCase)
```

**With Assisted Injection:**
```kotlin
viewModel = TeamsListViewModel(useCase, "French Ligue 1")  // Clean, direct!
```

### Why StandardTestDispatcher vs UnconfinedTestDispatcher?

- **StandardTestDispatcher**: For ViewModels with timing concerns (debouncing, delays)
  - Requires `advanceTimeBy()` and `advanceUntilIdle()`
  - Gives full control over virtual time

- **UnconfinedTestDispatcher**: For simple async code (repositories, use cases)
  - Executes immediately
  - Faster, less boilerplate

### Why Separate UiState Files?

Following the "each class in own file" rule:
- Better code organization
- Easier navigation
- Clearer separation of concerns
- Follows Android best practices

## 📄 License

This project is a technical test for FDJ.

## 👤 Author

**Jérôme Dusanter**
- GitHub: [@jerome-dusanter](https://github.com/jerome-dusanter)
- Email: dusanter.jerome@gmail.com

---

🤖 Built with assistance from [Claude Code](https://claude.com/claude-code)
