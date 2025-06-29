# DroidWord - Word Cards Android App

[![Android](https://img.shields.io/badge/Android-API%2029+-green.svg)](https://developer.android.com/about/versions/android-10)
[![Gradle](https://img.shields.io/badge/Gradle-8.9-blue.svg)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A simple and efficient Android application for creating and managing word cards for learning and memorization. DroidWord allows users to create keyword-value pairs, navigate through them, and export/import data for backup purposes.

## 📱 Features

- **Word Card Management**: Create, edit, and delete keyword-value pairs
- **Navigation**: Forward/backward navigation through word cards
- **Display Mode**: Show/hide values to test your memory
- **Data Export/Import**: Backup and restore your word cards
- **Reverse Mode**: Switch between keyword and value display
- **Seek Bar Navigation**: Quick navigation through all cards
- **Status Persistence**: App remembers your current position and settings

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK API 29+ (Android 10)
- Java 19
- Gradle 8.9

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/droid_word.git
   cd droid_word
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the cloned directory and select it

3. **Build and Run**
   - Connect an Android device or start an emulator
   - Click the "Run" button (green play icon) in Android Studio
   - The app will be installed and launched on your device

### Building from Command Line

```bash
# Navigate to project directory
cd droid_word

# Build the project
./gradlew build

# Install on connected device
./gradlew installDebug
```

## 📖 Usage

### Basic Operations

1. **Adding New Words**
   - Tap "ADD NEW WORD" button
   - Enter keyword and value
   - Tap "SAVE" to store the word card

2. **Viewing Words**
   - Use "DISPLAY VALUE" to show the value for the current keyword
   - Use "FORWARD" and "BACKWARD" buttons to navigate
   - Use the seek bar for quick navigation

3. **Managing Words**
   - "DELETE THIS WORD" removes the current word card
   - Menu options provide additional functionality

### Menu Options

- **Reverse Keyword**: Switch display between keyword and value
- **Back to First**: Return to the first word card
- **Export Data**: Save all word cards to a CSV file
- **Delete All Words**: Reset the application (with confirmation)

## 🏗️ Project Structure

```
droid_word/
├── app/
│   ├── src/main/
│   │   ├── java/droid/word/
│   │   │   ├── WordActivity.java      # Main activity
│   │   │   ├── WordDBHelper.java      # Database helper
│   │   │   └── WordEntity.java        # Data model
│   │   ├── res/
│   │   │   ├── layout/main.xml        # Main UI layout
│   │   │   ├── values/strings.xml     # English strings
│   │   │   └── values-ja/strings.xml  # Japanese strings
│   │   └── AndroidManifest.xml        # App manifest
│   └── build.gradle                   # App-level build config
├── build.gradle                       # Project-level build config
└── gradle/wrapper/                    # Gradle wrapper
```

## 🛠️ Technical Details

### Architecture
- **Language**: Java
- **Database**: SQLite with custom helper class
- **UI**: Traditional Android Views (no modern UI libraries)
- **Minimum SDK**: API 29 (Android 10)
- **Target SDK**: API 33 (Android 13)

### Key Components

- **WordActivity**: Main activity handling UI interactions and business logic
- **WordDBHelper**: SQLite database helper for CRUD operations
- **WordEntity**: Data model representing a word card
- **CSV Import/Export**: File-based data persistence

## 📱 Screenshots

*Screenshots would be added here showing the app interface*

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines

- Follow Android development best practices
- Maintain backward compatibility with API 29+
- Add appropriate error handling
- Include Japanese localization for new strings
- Test on both phone and tablet layouts

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**yasupong** - *Initial work*

## 🙏 Acknowledgments

- Android Developer Documentation
- SQLite for data persistence
- Gradle build system

## 📞 Support

If you encounter any issues or have questions:

1. Check the [Issues](../../issues) page for existing problems
2. Create a new issue with detailed information
3. Include device information and Android version

---

**Note**: This app is designed for educational purposes and personal word card management. For production use, consider adding additional security measures and cloud backup options. 