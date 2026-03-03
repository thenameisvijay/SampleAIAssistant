# SampleAIAssistant

SampleAIAssistant is a **Compose Multiplatform** application (Android & iOS) designed to interact with an AI model hosted on a **Raspberry Pi**. The app provides a seamless chat interface and supports speech-to-text capabilities for a hands-free AI assistant experience.

## 🚀 Features

- **Compose Multiplatform**: Shared UI and logic across Android and iOS using Jetpack Compose.
- **AI Chat Interface**: Real-time communication with a self-hosted AI model.
- **Speech-to-Text**: Voice-enabled input for interacting with the assistant.
- **Local Persistence**: Chat history is stored locally for offline access.
- **Raspberry Pi Integration**: Connects to a custom API service hosting an AI model on a Raspberry Pi.

## 🛠️ Tech Stack

- **UI**: Compose Multiplatform
- **Dependency Injection**: Koin
- **Networking**: Ktor
- **Local Database**: SQLDelight (DatabaseDriverFactory)
- **Concurrency**: Kotlin Coroutines & Flow
- **Architecture**: Clean Architecture (Data, Domain, Presentation)

## 📁 Project Structure

- `composeApp/src/commonMain`: Shared logic, data models, repositories, and UI components.
- `composeApp/src/androidMain`: Android-specific implementations (e.g., Database Driver, MainApplication).
- `composeApp/src/iosMain`: iOS-specific implementations and entry points.
- `data/`: Network services (`AIChatApiService`) and repository implementations.
- `domain/`: Business logic and Use Cases.
- `presentation/`: ViewModels and Compose UI screens.

## ⚙️ Setup & Configuration

### AI Service URL
The application communicates with the AI model via a REST API. You need to configure the `BASE_URL` in `AIChatApiService.kt`:

```kotlin
// composeApp/src/commonMain/kotlin/com/vj/sampleaiassistant/data/remote/AIChatApiService.kt
const val BASE_URL = "http://<YOUR_RASPBERRY_PI_IP>:5000"
```

### Build & Run

#### Android
1. Open the project in Android Studio.
2. Select the `composeApp` run configuration.
3. Click **Run**.

Alternatively, via terminal:
```bash
./gradlew :composeApp:assembleDebug
```

#### iOS
1. Open the `iosApp/iosApp.xcworkspace` in Xcode OR use the `iosApp` run configuration in Android Studio.
2. Ensure you have a simulator or device selected.
3. Click **Run**.

## 📝 License

Created by [Vijay](https://github.com/thenameisvijay).
