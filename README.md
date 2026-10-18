# Calculator

A native Kotlin Android calculator with a dark Material interface, large keys, and locally saved history. No network permissions or services are needed.

## Features

- Addition, subtraction, multiplication, and division with normal operator precedence.
- Parentheses, unary negative numbers, decimals, and percentages.
- Clear and backspace controls, horizontal scrolling for long expressions.
- Last 50 successful calculations saved locally; tap a history entry to reuse its result.
- Expression and result restored after rotation or activity recreation.
- Accessible button labels and a scrollable layout for smaller screens.

Percent converts the preceding value to a fraction: `5% = 0.05`, and `200 × 5% = 10`. Multiplication before an opening parenthesis is inserted automatically. Results use floating-point arithmetic rounded to 12 significant digits; this is not a financial precision calculator.

## Build and run

Requirements: JDK 17, Android SDK 35, and Gradle 8.9. Minimum device version is Android 7.0 (API 24).

Open the project in Android Studio, configure JDK 17 and Gradle 8.9 if prompted, sync, then run the `app` configuration on a device or emulator.

Command-line build with an installed Gradle 8.9:

```sh
gradle :app:assembleDebug
gradle :app:testDebugUnitTest
```

Set `ANDROID_HOME` to your SDK directory or configure `sdk.dir` in `local.properties`. The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

Wrapper distribution settings are included. To generate the wrapper scripts and binary JAR using installed Gradle:

```sh
gradle wrapper --gradle-version 8.9
./gradlew :app:assembleDebug
```

## Stack

Kotlin 2.0.21, Android Gradle Plugin 8.7.3, AndroidX AppCompat, and Material Components. Compile/target SDK 35.
