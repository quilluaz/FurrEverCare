# FurrEverCare

FurrEverCare is a mobile and web-based application designed to help pet owners manage their pets’ health efficiently. The app provides tools for tracking medical records, handling emergencies, and accessing pet health resources to ensure pets receive the best possible care.

## Features

<details>
  <summary>Health Tracking</summary>
  
  #### ● Log treatments, medications, and vet appointments.
</details>

<details>
  <summary>Emergency Pet Profile Card</summary>
  
  #### ● Quick access to critical health information in emergencies.
</details>

<details>
  <summary>Real-time Notifications</summary>
  
  #### ● Treatment schedules, check-ups, and medication alerts.
</details>

<details>
  <summary>Interactive Pet Wellness Timeline</summary>
  
  #### ● Visually track medical history and health progress.
</details>

<details>
  <summary>AI-Powered Symptom Checker</summary>
  
  #### ● Analyze symptoms and suggest potential conditions.
</details>

<details>
  <summary>Searchable Pet Health Encyclopedia</summary>
  
  #### ● Explore conditions, symptoms, and treatments.
</details>

<details>
  <summary>First-Aid Visual Instructions</summary>
  
  #### ● Offline emergency guides with step-by-step instructions.
</details>

<details>
  <summary>Offline Mode Support</summary>
  
  #### ● Access emergency guides without an internet connection.
</details>

## Out of Scope

#### ● Live veterinary consultations or messaging with vets.
#### ● Financial transactions for veterinary services.
#### ● Advanced biometric authentication beyond facial recognition.

## Tech Stack

#### Backend: 
Java Spring Boot

#### Frontend: 
React.js

#### Mobile: 
Kotlin (Android)

#### Database: 
Firebase (Firestore & Cloud Functions)

#### Authentication: 
Firebase Auth, Multi-Factor Authentication (MFA)

#### Hosting: 
Firebase Hosting & Cloud Functions

#### API Integrations: 
Google Maps (Location Services), DeepSeek AI (Symptom Checker)

## Project Structure

```
FurrEverCare/
│── backend/          # Java Spring Boot backend
│── web/              # React frontend
│── mobile/           # Kotlin Android app
│── firebase/         # Firebase Firestore & Cloud Functions
│── global/           # Logos, fonts, and reusable UI assets
│── docs/             # Documentation (SRS, ERD, etc.)
```

## Installation & Setup

### 1. Clone the Repository
```sh
git clone https://github.com/your-repo/FurrEverCare.git
cd FurrEverCare
```

### 2. Backend Setup (Spring Boot)
```sh
cd backend
mvn clean install
mvn spring-boot:run
```

### 3. Web App Setup (React.js)
```sh
cd web
npm install
npm run dev
```

### 4. Mobile App Setup (Kotlin)
Open `mobile/` in Android Studio, sync Gradle, and run the app.

### 5. Firebase Setup
Configure Firestore rules in `firebase/firestore-rules/`.
Deploy cloud functions:
```sh
cd firebase/cloud-functions
firebase deploy
```

## License
This project is created for educational purposes only and is not intended for commercial or real-world application use. Standard licensing does not apply.
