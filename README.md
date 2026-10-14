# TeachMate 360 📚

**An Android application designed to simplify academic management for teachers and students.**

[![Download APK](https://img.shields.io/badge/Download-Android%20APK-brightgreen)](https://github.com/pranavpatil1620/TeachMate360/releases/latest)

## 📖 About the Project

TeachMate 360 is an Android application that brings common classroom activities into one place. It provides separate teacher and student interfaces for accessing academic information and managing classroom tasks.

## ✨ Features

- **Teacher and Student Authentication** — Separate login and registration screens.
- **Attendance Management** — Attendance-related functionality for teachers and students.
- **Marks Management** — Manage and view academic marks.
- **Assignment Management** — Assignment-related operations and student access.
- **Notice Board** — Share and view academic notices.
- **Profile Management** — Dedicated teacher and student profile screens.
- **Role-Based Navigation** — Separate dashboards for teachers and students.

## 🛠️ Technology Stack

- **Platform:** Android
- **Languages:** Java, Kotlin
- **UI Design:** XML Layouts
- **Build System:** Gradle (Kotlin DSL)
- **Backend:** Firebase Authentication and Firebase Realtime Database

## 📱 Download the App

Download the latest available demo APK from GitHub Releases:

**[⬇️ Download TeachMate 360 APK](https://github.com/pranavpatil1620/TeachMate360/releases/latest)**

> **Note:** This is a demo pre-release. Test the app before using it. Firebase-backed features require appropriate Firebase configuration and security rules.

## 🚀 Getting Started

### Prerequisites

- Android Studio
- Android SDK compatible with the project
- A Firebase project configured for the application

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/pranavpatil1620/TeachMate360.git
   ```

2. Open the cloned project in Android Studio.
3. Allow Gradle to sync and download dependencies.
4. Register the Android app in your Firebase project.
5. Add your own `google-services.json` file inside the `app/` directory.
6. Enable the Firebase services required by the application and configure secure database rules.
7. Run the app on an Android emulator or compatible device.

## 📂 Project Modules

| Module | Description |
|---|---|
| Teacher | Login, registration, dashboard and profile |
| Student | Login, registration, dashboard and profile |
| Attendance | Attendance-related features |
| Marks | Academic marks |
| Assignments | Assignment management and access |
| Notices | Academic announcements |

## 🔐 Firebase Configuration

The `app/google-services.json` configuration file is not included in this repository. Configure your own Firebase project before running the application.

Never commit Firebase service-account private keys, signing keys, passwords or other sensitive credentials. Protect Firebase data using appropriate Authentication and Realtime Database security rules.

## 👨‍💻 Developer

**Pranav Patil**

- GitHub: [@pranavpatil1620](https://github.com/pranavpatil1620)
- Repository: [TeachMate360](https://github.com/pranavpatil1620/TeachMate360)

## 📄 License

No license has been specified. All rights are reserved by the repository owner unless a license is added.
