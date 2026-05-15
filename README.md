# 🤱 Matru-Sneh Health
### Pocket Pregnancy Guide for Rural Mothers

An offline-first Android app designed to support rural expectant mothers 
in tracking their maternal health without needing internet access. 
Serves as a digital backup to the physical Mother-Child Health (MCH) card.

---

## 📌 Problem Statement
Rural expectant mothers rely on physical Mother-Child Health (MCH) cards 
that are often lost or damaged. They lack a simple, offline tool to track 
fetal movements, daily nutrition, upcoming check-up dates, and danger signs 
— all without needing internet access.

---

## ✨ Features

### 👶 Kick Counter
- Large single tap button to record fetal movements
- Debounce protection (500ms) to prevent double counting
- Timestamped kick events stored in local Room DB
- Weekly Kicks per Hour visualization table (last 7 days)

### 📅 Check-up Countdown
- Enter next Scan and Vaccination dates via date picker
- Displays days remaining on the home dashboard
- Background WorkManager notifications — fires even when app is closed

### 🥗 Nutrition Plate
- Daily checklist of 10+ local foods:
  Ragi, Greens (Palak/Methi), Pulses, Milk, Eggs,
  Banana, Drumstick, Jaggery, Sesame, Groundnuts
- Progress indicator showing daily completion percentage
- Auto-resets at midnight via WorkManager

### 🚨 Danger Sign Alerts
- Tracks 6 critical danger signs:
  1. Severe swelling of face/hands/feet
  2. Severe headache
  3. Blurred vision
  4. Reduced/absent fetal movement
  5. Vaginal bleeding
  6. High fever
- Full-screen red alert with "Go to hospital immediately" message
- Displays helpline number
- Dismissible only by explicit user action

### 🙋 Onboarding / Profile Setup
- First-launch screen to enter gestational week (1–42)
- Personalizes app content and context
- Updatable anytime from Settings screen

### 🏠 Home Dashboard
- Central screen linking all four core features
- Displays upcoming appointment countdowns at a glance
- Bottom navigation for easy access

### 💾 Offline Data Persistence
- All data stored locally in Room DB (SQLite)
- Survives device reboots without data loss
- No data transmitted to any remote server

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Language | Kotlin |
| IDE | Android Studio |
| Architecture | MVVM |
| Database | Room DB (SQLite) |
| UI Framework | Jetpack Compose / XML |
| Navigation | Jetpack Navigation Component |
| Background Tasks | WorkManager |
| Async Programming | Kotlin Coroutines |
| State Management | StateFlow |
| Notifications | Android NotificationManager |
| AI Assistance | Claude AI |

---

## 🗄️ Database Schema

| Table | Key Columns | Purpose |
|---|---|---|
| kick_log | id, timestamp_ms, date | Stores each kick event |
| appointment | id, type, date, work_tag | Stores appointment dates |
| nutrition_log | id, food_id, date, is_checked | Daily food checklist state |
| food_item | id, name, icon_res | Static list of food items |
| user_profile | id, gestational_week, name | User profile |

---

## 📱 Compatibility
- **Platform:** Android only
- **Minimum SDK:** Android 8.0 (API 26+)
- **Mode:** Fully offline — works in airplane mode

---

## 👤 Target Users
Rural expectant mothers who are likely first-time smartphone users 
with intermittent or no internet access, needing a simple maternal 
health tracking tool usable without any prior training.

---

## 🚀 Getting Started

1. Clone the repository
   git clone https://github.com/YourUsername/Matru-Sneh-Health.git

2. Open in Android Studio

3. Build and run on an Android device or emulator (API 26+)

---

## 📂 Project Structure
app/
├── data/
│   ├── db/          # Room DB, DAOs, Entities
│   └── repository/  # Repository classes
├── ui/
│   ├── home/        # Home Dashboard
│   ├── kick/        # Kick Counter
│   ├── nutrition/   # Nutrition Plate
│   ├── appointment/ # Check-up Countdown
│   ├── danger/      # Danger Sign Alerts
│   └── onboarding/  # Profile Setup
├── viewmodel/       # ViewModels (MVVM)
└── worker/          # WorkManager Workers

---

## 👩‍💻 Developed By
**Maria Saji**
USN: 4MN22CS026
Branch: Computer Science and Engineering
College: Maharaja Institute of Technology, Thandavapura

**Programme:** Android App Development using GenAI
**Organization:** MindMatrix Industry Readiness Programme
**Project:** #91 — Matru-Sneh Health

---

## 🙏 Acknowledgements
- MindMatrix for the internship opportunity
- Mentor: Mr. Tirumal Mutalikdesai
- Internal Guide: Prof. Poornima H, Dept. of CS&E
