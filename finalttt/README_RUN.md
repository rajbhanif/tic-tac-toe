# How to build / run

This repository is a **Gradle** Android project (Kotlin DSL).

If you previously tried `chmod +x mvnw` or `./mvnw`, that will fail because this project **does not use Maven**.
Use the Gradle wrapper instead: `./gradlew`.

## Option A — Android Studio (recommended)
1. Install Android Studio.
2. Open Android Studio → **File → Open…** → select this project folder (`finalttt`).
3. Let it sync (it may download Gradle + dependencies).
4. Click **Run** ▶️.

## Option B — Command line
> You need JDK 17+ and Android SDK installed.

### macOS / Linux
```bash
cd finalttt
chmod +x gradlew
./gradlew tasks
./gradlew assembleDebug
```

### Windows (PowerShell)
```powershell
cd finalttt
.\gradlew.bat tasks
.\gradlew.bat assembleDebug
```

## Common “wrapper” error fixes
- **`mvnw: No such file or directory`** → use `gradlew` (this project is Gradle).
- **`gradlew: Permission denied`** → run `chmod +x gradlew`.
- **`Could not find or load main class org.gradle.wrapper.GradleWrapperMain`** → make sure these files exist:
  - `gradle/wrapper/gradle-wrapper.jar`
  - `gradle/wrapper/gradle-wrapper.properties`
