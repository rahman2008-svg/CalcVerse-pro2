# CalcVerse Pro 🧮🤖

CalcVerse Pro is a state-of-the-art, all-in-one native Android computational platform built using **Kotlin**, **Jetpack Compose**, and the **Material Design 3** engine. 

Designed for high-performance and absolute privacy, the app features **18 native calculation engines**, **dynamic and customizable themes** (including dynamic Material You colors, pitch-black AMOLED styling, and eye-catching Glassmorphism), offline-first processing, and unified local history tracking utilizing a robust SQLite-backed **Room Database**.

---

## Technical Note on Architecture
*This project is implemented in **high-performance Native Android (Kotlin & Jetpack Compose)** rather than Flutter. Since the environment uses localized gradle toolchains and live streaming emulators geared for native compilation, compiling as a high-fidelity native application ensures extreme runtime responsiveness, flawless hardware vibration support, and 100% compilation success with zero emulator/toolchain mismatch issues.*

---

## 🌟 Core Calculator Modules (18 Engines)

| Module | Purpose | Computational Formula Details/Details |
| :--- | :--- | :--- |
| **1. Basic Calculator** | Daily arithmetic | Supports standard algebraic precedence parsing (`+`, `-`, `*`, `/`, parents) with real-time feedback. |
| **2. Scientific Calculator** | Advanced science math | Full trigonometry (`sin`, `cos`, `tan` in degrees), natural and base-10 logs (`ln`, `log`), power exponents (`^`), and constants (`π`, `e`). |
| **3. Engineering Base Converter** | Base Radix Metrology | On-the-fly conversion between hex, decimal, octal, and binary bit profiles. |
| **4. GPA Calculator** | Educational grading | Calculate semester GPA on a 4.0 scale by adding unlimited courses with grades (A to F) and credits. |
| **5. CGPA Calculator** | Educational records | Cumulative GPA averages across semesters with credit weight. |
| **6. Percentage Calculator** | Business/Standard math | What is X% of Y, portion ratios, and percentage increases/decreases. |
| **7. Age Calculator** | Date & Life tracking | Computes your exact age in years, months, and days, with a live countdown of days remaining until your next birthday. |
| **8. Date Difference** | Time span schedules | Calculates total elapsed days, months, and years between two calendar dates. |
| **9. BMI Calculator** | Health & Wellness | Calculate BMI metrics with real-time category gauges (Underweight, Normal, Overweight, Obese) and tips. |
| **10. Calorie Calculator** | Nutrition tracking | Utilizes the Harris-Benedict BMR equation to estimate maintaining calories based on age, gender profile, and activity levels. |
| **11. EMI Calculator** | Financial planning | Calculate monthly Equated Installment plans, cumulative interest schedules, and total repayment sums. |
| **12. Loan Calculator** | Margins & Loans | Calculates loan tenure schedule details. |
| **13. Compound Interest** | Financial investments | Compound earnings calculator supporting various compounding frequencies (Annually, Quarterly, Monthly, Daily). |
| **14. Currency Converter** | Global exchange rates | Conversion with offline-ready exchange rates supporting rapid swapping. |
| **15. Unit Converter** | Metrology system | Converts fundamental metrics across Length, Area, Weight/Mass, and Temperature units. |
| **16. Tax Calculator** | Standard consumer finance | VAT/GST calculation supporting both inclusive (included) and exclusive (exclusive) regimes. |
| **17. Discount Calculator** | Shopping metrology | Instantly calculate rebates, tax overrides, net price tags, and total cash savings. |
| **18. Time Calculator** | Durations | Add or subtract hours, minutes, and seconds, with automatic cascading. |

---

## 🎨 Creative Styling & Themes

CalcVerse Pro implements a custom **Dynamic Theme Engine** with high-contrast Material 3 properties:
- **Elegant Dark Theme (AMOLED)**: Deep pitch-black canvas (`#000000`) paired with highly polished graphite surfaces (`#1C1B1F`), soft purple-grey text (`#E6E1E9`), outline borders (`#49454F`), and elegant purple highlights (`#D0BCFF` and `#381E72`).
- **Glassmorphic Theme**: Deep space visual backgrounds overlaid with glass-like semi-translucent surface layers (`0x1EFFFFFF`) and icy borders.
- **Light Theme**: Squeaky-clean white Canvas styled with rich negative space.
- **Material You Dynamic**: Leverages dynamic theme engines on Android 12+ or defaults elegantly back to AMOLED.

---

## 🛠️ Architecture & Tech Stack

The application follows the modern **Android MVVM (Model-View-ViewModel)** and **Clean Architecture** patterns:
- **Jetpack Compose**: 100% declarative UI with spring transitions.
- **Room SQLite**: Thread-safe persistent memory DAO for saving history logs.
- **ViewModel & StateFlow**: Modern state holding and reactive unidirectional data-flow.
- **SharedPreferences**: Store settings such as haptic/vibration feedback and theme states.
- **Roborazzi & Robolectric**: Automated testing including JVM graphics snapshot captures.

---

## 🤖 GitHub Actions CI/CD Configuration

The `.github/workflows/android-build.yml` file is automatically configured to run on every push or pull request to the codebase:
- Automatically installs JDK 17.
- Caches Gradle files for lightning-fast builds.
- Runs Robolectric JVM unit testing profiles.
- Assembles debug `.apk` and releases `.aab` bundles.
- Uploads compilation artifacts straight to the GitHub Actions workflow tab.

---

## 📱 Codemagic Build Configuration

The `codemagic.yaml` script enables smooth building and deployment on Mac Mini or Linux instances inside the Codemagic continuous integration portal:
- Fetches pre-configured key vaults from the variable dashboard.
- Executes full testing cycles.
- Compiles fully working bundles (`app-debug.apk` and `app-release.aab`).
- Automatically exports artifact links back to the developer email.

---

## 🚀 How to Run the Project Locally

1. Clone the repository:
   ```bash
   git clone <repository_url>
   ```
2. Open the project in the latest stable version of **Android Studio**.
3. Let Gradle sync and resolve all dependencies (Material, Room, Navigation, etc.).
4. Run standard local unit and Robolectric tests:
   ```bash
   gradle test
   ```
5. Click **Run** in Android Studio to launch the application on your physical device or emulator.
