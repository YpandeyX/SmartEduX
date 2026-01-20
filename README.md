# 📱 SmartEduX (TeamSquadX)

SmartEduX is a **smart classroom & attendance management Android application** designed to prevent proxy attendance using **QR codes, geo-fencing, and Firebase backend services**.  
The system supports **students and teachers**, real-time attendance tracking, and role-based notifications.

---
For Testing
Email id - eduxsmart@gmail.com
Password - 123456
---

## 🚀 Features

### 👨‍🎓 Student Features
- Email & Password Login
- Google Account Linking (optional)
- Scan Teacher QR Code to mark attendance
- Geo-fenced attendance (only within classroom area)
- Attendance percentage tracking
- College / Branch / Section based notifications
- Editable profile (local storage profile photo)

### 👩‍🏫 Teacher Features
- Generate QR codes for classes
- Select subject & section
- Automatic session creation
- QR validity with timestamp
- Teacher location stored for geo-fencing
- Prevent duplicate attendance

### 🔐 Security & Anti-Fraud
- Firebase Authentication
- Email verification check
- Time-limited QR codes
- GPS-based geo-fencing
- One attendance per session
- Fake GPS detection (basic)

---

## 🧱 Tech Stack

### 📱 Android
- Kotlin
- XML UI
- ViewBinding
- CameraX
- RecyclerView

### ☁️ Firebase
- Firebase Authentication
- Firebase Firestore
- Firebase Cloud Messaging (notifications)
- Firebase Security Rules

### 📍 Google Services
- Google Sign-In (OAuth)
- Google ML Kit (QR Scanning)
- Google Location Services (Geo-fencing logic)

---

## 🗂️ Project Architecture

```text
Student App
 ├── Login / Register
 ├── Profile Setup
 ├── Dashboard
 │    ├── Attendance %
 │    ├── Scan QR
 │    └── Notifications
 ├── AttendanceActivity
 │    ├── QR Scan
 │    ├── Location Check
 │    └── Firestore Save
 └── Profile & Settings

Teacher App (Role-based)
 ├── Teacher QR Generator
 ├── Session Creation
 ├── Location Capture
 └── Firestore Sync



🧭 Attendance Flow (How It Works)

Teacher

Opens QR Generator

Selects Subject & Section

Location + timestamp saved to Firestore

QR code generated

Student

Opens Scan Attendance

Location fetched

Scans QR code

App checks:

QR validity

Time limit

Distance from teacher

Attendance marked if valid

🔔 Notification System

Notifications are divided into levels:

College Level → Visible to all students

Branch Level → Visible to specific branch

Section Level → Visible only to that section

Firestore structure ensures targeted delivery.

🗃️ Firestore Data Structure (Simplified)
users/
 └── userId
     ├── name
     ├── rollNumber
     ├── branch
     ├── section
     └── profileCompleted

classes/
 └── date
     └── subject
         └── section
             └── sessions
                 └── sessionId

attendance/
 └── date
     └── subject
         └── section
             └── sessions
                 └── sessionId
                     └── students
                         └── userId

notifications/
 └── notificationId
     ├── title
     ├── message
     ├── level
     ├── branch
     ├── section
     └── timestamp

🔑 Authentication Flow

Default login via Email & Password

Email verification required

Optional Google Account Linking

Once linked → Google Sign-In enabled

Secure Firebase UID mapping

🧪 Testing & Deployment

Tested on physical Android devices

Debug & Release APK supported

SHA-1 configured for Google Sign-In

Firebase rules tested for role-based access

📌 Future Improvements

Advanced fake GPS detection

Admin dashboard (web)

Analytics for attendance trends

Offline attendance sync

Multi-campus support

AI-based attendance insights

👨‍💻 Developed By

Team SquadX
Android | Firebase | Google Technologies


⭐ If you like this project, give it a star on GitHub!
