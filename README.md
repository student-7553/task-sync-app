# TaskSync - Android Task Management App with Offline Sync

TaskSync is a modern, Kotlin-based Android application for efficient task management. It features a robust offline-first architecture with cloud synchronization using HTTP Firebase Cloud Functions (along with Kafka event logging), built with Jetpack Compose and Material Design 3.

## 🚀 Features

- **Offline-First Architecture**: Seamlessly manage tasks without an internet connection using Room Database.
- **Modern UI**: Intuitive and responsive interface built with Jetpack Compose and Material 3 dynamic colors.
- **Priority Management**: Assign priorities (Low, Medium, High) to tasks with dynamic sorting.
- **Smart Reminders**: Local notifications for upcoming tasks using WorkManager.
- **Data Export**: Export your tasks to a structured JSON format for backups.
- **CI/CD Integrated**: Automated build and test pipeline.

## 🏗️ Architecture

The project follows the **Clean Architecture** and **MVVM** (Model-View-ViewModel) patterns:

- **UI Layer**: Jetpack Compose for declarative UI, Hilt for Dependency Injection, and ViewModels for state management.
- **Domain/Data Layer**: Repository pattern to abstract local (Room) and remote (Firebase Functions) data sources.
- **Background Tasks**: WorkManager for reliable notification scheduling.

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Design System**: Material Design 3
- **Dependency Injection**: Dagger Hilt
- **Local Database**: Room
- **Cloud Backend**: Firebase Functions (Node.js) & Firestore
- **Audit Logging**: Apache Kafka
- **Asynchronous Flow**: Coroutines & Flow
- **Background Work**: WorkManager
- **Testing**: JUnit, MockK, Compose UI Test
- **CI/CD**: GitHub Actions

## 🚦 Getting Started

### Prerequisites

- Android Studio Iguana or newer
- JDK 17
- A Firebase project (for cloud sync)

### Setup Instructions

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/task-sync-app.git
   ```

2. **Add Firebase**:
   - Go to the [Firebase Console](https://console.firebase.google.com/).
   - Add a new Android app with the package name `com.example.tasksync`.
   - Download the `google-services.json` and place it in the `app/` directory.

3. **Backend Setup (Firebase Functions & Kafka)**:
   - Navigate to the `functions/` directory.
   - Run `npm install` to install dependencies.
   - Set the `KAFKA_BROKERS` and `KAFKA_TOPIC` environment variables if you are connecting to a remote Kafka cluster (defaults to `localhost:9092` and `firebase-function-logs`).
   - Run `firebase deploy --only functions` to deploy the backend.

4. **Build and Run**:
   - Open the project in Android Studio.
   - Sync Gradle files.
   - Run the app on an emulator or physical device.

## 🧪 Testing

Run unit tests using Gradle:
```bash
./gradlew test
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
