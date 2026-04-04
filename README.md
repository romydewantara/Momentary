# Momentary
[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![MVVM](https://img.shields.io/badge/MVVM-Architecture-FF6F00?style=for-the-badge)](https://developer.android.com/topic/architecture)
[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Retrofit](https://img.shields.io/badge/Retrofit-009688?style=for-the-badge)](https://square.github.io/retrofit/)
[![Coil](https://img.shields.io/badge/Coil-673AB7?style=for-the-badge)](https://coil-kt.github.io/coil/)
[![SharedPreferences](https://img.shields.io/badge/SharedPreferences-795548?style=for-the-badge)](https://developer.android.com/reference/android/content/SharedPreferences)

Momentary is a simple Android application for sharing visual stories. Users can register, log in, browse stories from other users, and upload their own stories with images.  
This project was developed to fulfill the graduation requirements for the Belajar Pengembangan Aplikasi Android Intermediate course on Dicoding.

Developer: Romy Dewantara

## Features

**Login & Registration**  
User authentication with email and password.

**Session Management**  
Login session and token are stored using SharedPreferences so users remain logged in when reopening the app.

**Story List & Detail**  
Displays a list of stories from the API and allows users to view detailed information including photo and description.

**Add Story**  
Users can upload a new story with a photo and description. Images can be selected from the gallery or captured using the camera.

**Custom Input Validation**  
Custom input fields validate email format and password length (minimum 8 characters) with instant error messages.

**Animations**  
The application uses animation to improve the user experience.

**Localization**  
Supports multi-language configuration.

**API Feedback**  
Displays loading indicators, error messages, and empty-state messages during API interactions.

## Technologies Used

**Kotlin** – Main programming language  
**Jetpack Compose** – Modern Android UI toolkit  
**MVVM Architecture** – Separation of UI, business logic, and data layers
**Hilt (Dagger)** – Dependency Injection
**ViewModel & StateFlow** – State and lifecycle management  
**Retrofit** – API communication
**UCrop** – Image cropping
**CameraX** – Camera integration
**App Widget (StackWidget)** – Home screen widget
**SharedPreferences** – Session and token storage  
**Coil / Glide** – Image loading

## Prerequisites

Android Studio  
Android SDK  
Internet connection for API access

## License

MIT License

Copyright (c) 2026 Romy Dewantara

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction.