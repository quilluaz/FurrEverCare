<<<<<<< HEAD
# React + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react/README.md) uses [Babel](https://babeljs.io/) for Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react-swc) uses [SWC](https://swc.rs/) for Fast Refresh

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript and enable type-aware lint rules. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.
=======
# FurrEverCare

FurrEverCare is a mobile and web-based application designed to help pet owners manage their pets’ health efficiently. The app provides tools for tracking medical records, handling emergencies, and accessing pet health resources to ensure pets receive the best possible care.

## Features

<details>
  <summary>Feature 1</summary>
  
  #### Health Tracking:  
  ##### Log treatments, medications, and vet appointments.
</details>

<details>
  <summary>Feature 2</summary>
  
  #### Emergency Pet Profile Card:  
  ##### Quick access to critical health information in emergencies.
</details>

<details>
  <summary>Feature 3</summary>
  
  #### Real-time Notifications:  
  ##### Treatment schedules, check-ups, and medication alerts.
</details>

<details>
  <summary>Feature 4</summary>
  
  #### Interactive Pet Wellness Timeline:  
  ##### Visually track medical history and health progress.
</details>

<details>
  <summary>Feature 5</summary>
  
  #### AI-Powered Symptom Checker:  
  ##### Analyze symptoms and suggest potential conditions.
</details>

<details>
  <summary>Feature 6</summary>
  
  #### Searchable Pet Health Encyclopedia:  
  ##### Explore conditions, symptoms, and treatments.
</details>

<details>
  <summary>Feature 7</summary>
  
  #### First-Aid Visual Instructions:  
  ##### Offline emergency guides with step-by-step instructions.
</details>

<details>
  <summary>Feature 8</summary>
  
  #### Offline Mode Support:  
  ##### Access emergency guides without an internet connection.
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
│── frontend-web/              # React frontend
│── frontend-mobile/           # Kotlin Android app
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

## Links
#### [Figma](https://www.figma.com/design/AANK9bVmg8d9unJQcXKdnY/FurrEverCare-UI%2FUX?node-id=0-1&p=f)
  
#### [Diagram]()

## Developers Profiles
- **Genosolango, Selina Mae** - Web Frontend Dev | [GitHub](https://github.com/selmvg)
- **Melocoton, Jeric Kiel** - Backend Dev | [GitHub](https://github.com/serkiel)
- **Quilo, Jan Isaac** - Mobile Frontend Dev | [GitHub](https://github.com/quilluaz)

## License
This project is created for educational purposes only and is not intended for commercial or real-world application use. Standard licensing does not apply.
>>>>>>> 8d43a5a0f805fd1077cc5d62e56abb6ab492fc28
