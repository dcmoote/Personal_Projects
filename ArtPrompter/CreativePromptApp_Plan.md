# Creative Prompt App — Android Planning Document

**Stack:** Java · Native Android · Fully On-Device Storage · Hybrid Prompt Generation
**Date:** March 2026

---

## 1. Project Overview

A native Android app that:
1. Collects user creative preferences on first launch (onboarding)
2. Delivers a daily prompt — either a short story writing idea or an art idea — tailored to those preferences
3. Uses Claude AI (via API) to generate fresh prompts when online, and falls back to a bundled local library when offline
4. Allows preferences to be modified at any time from a Settings screen

---

## 2. Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| Min SDK | Android 8.0 (API 26) ~95% device coverage |
| Architecture Pattern | MVVM (Model-View-ViewModel) |
| UI | Material Design 3 (MDC-Android) |
| Local Storage | Room (SQLite ORM) + SharedPreferences |
| Networking | Retrofit 2 + OkHttp + Gson |
| Background Tasks | WorkManager (daily prompt/notification scheduling) |
| Notifications | NotificationManager + WorkManager |
| Build | Gradle (Groovy DSL) |

No external dependency injection framework is needed at this scale — manual DI via a simple `AppContainer` singleton keeps it straightforward for a Java project.

---

## 3. App Screens & User Flow

### First Launch — Onboarding Flow
```
Splash Screen
    └─> Welcome Screen ("Let's set up your creative space")
            └─> Step 1: Creative Type
                    [ Writing ] [ Art ] [ Both ]
            └─> Step 2a: Writing Genres  (shown if Writing or Both)
                    Multi-select: Fantasy, Sci-Fi, Horror, Romance, Mystery, Thriller
            └─> Step 2b: Art Medium  (shown if Art or Both)
                    Multi-select: Sketch, Watercolor, Digital, Oil, Acrylic, Ink,
                                  Pixel Art, Mixed Media
            └─> Step 2c: Art Subject  (shown if Art or Both)
                    [ People & Portraits ] [ Landscapes & Nature ] [ Both ]
            └─> Step 3: Reminder (optional)
                    Toggle daily reminder ON/OFF
                    Time picker (default: 9:00 AM)
            └─> Finish → Home Screen
```
Onboarding is shown only once. After completion, a flag is saved to `SharedPreferences` so the user goes directly to Home on subsequent launches.

### Home Screen
- Displays today's prompt in a card (type badge, genre tag, prompt text)
- "New Prompt" button to regenerate
- Small indicator: "✦ AI-generated" or "📚 From library" so users know the source
- Navigation bar linking to History and Settings

### History Screen
- Scrollable list of previously generated prompts (stored in Room DB)
- Filterable by type (Writing / Art)

### Settings Screen
- All onboarding preferences are re-editable here
- Reminder toggle and time picker
- "Clear history" option
- API toggle: "Use AI prompts when online" (on by default)
- **Appearance toggle: Light / Dark / System default**

---

## 4. UI Color System — Light & Dark Mode

Inspired by popular creative apps (Bear, Day One, Adobe Fresco, Procreate) and grounded in Material Design 3 color roles. The palette uses **warm terracotta as the primary accent** — associated with creativity and energy — balanced against a **cool slate blue secondary** (complementary colors on the color wheel), with warm neutral backgrounds that feel inviting rather than clinical.

### Light Mode

| Role | Hex | Usage |
|---|---|---|
| Background | `#FAF8F5` | Main screen background (warm off-white, not harsh) |
| Surface | `#FFFFFF` | Cards, dialogs, bottom sheets |
| Surface Variant | `#F0EDE8` | Chip backgrounds, input fields |
| Primary | `#C1603A` | Buttons, active icons, prompt card accent bar |
| On Primary | `#FFFFFF` | Text/icons on primary-colored surfaces |
| Secondary | `#5B7FA6` | Secondary buttons, badges, tags |
| On Secondary | `#FFFFFF` | Text/icons on secondary-colored surfaces |
| Tertiary | `#7D6B4F` | Subtle accents, dividers, writing genre chips |
| Text Primary | `#1C1C1E` | Body text, headings |
| Text Secondary | `#6B6B70` | Captions, hints, metadata |
| Divider | `#E5E2DC` | List separators |

### Dark Mode

| Role | Hex | Usage |
|---|---|---|
| Background | `#16161A` | Main screen background (near-black, slight warm tint) |
| Surface | `#1E1E24` | Cards, dialogs, bottom sheets |
| Surface Variant | `#26262E` | Chip backgrounds, input fields |
| Primary | `#E8845A` | Buttons, active icons (lightened for contrast on dark bg) |
| On Primary | `#1C1C1E` | Text/icons on primary-colored surfaces |
| Secondary | `#7AABD4` | Secondary buttons, badges, tags (lightened) |
| On Secondary | `#1C1C1E` | Text/icons on secondary-colored surfaces |
| Tertiary | `#B09A7F` | Subtle accents (lightened for dark bg) |
| Text Primary | `#F4F1EC` | Body text, headings (warm off-white) |
| Text Secondary | `#9B9BA3` | Captions, hints, metadata |
| Divider | `#2E2E38` | List separators |

### Contrast Ratios (WCAG Compliance)
All combinations meet at minimum **WCAG AA (4.5:1)** for normal text:

| Combination | Ratio | Grade |
|---|---|---|
| Text `#1C1C1E` on Background `#FAF8F5` | ~17:1 | AAA |
| White on Primary `#C1603A` | ~4.6:1 | AA |
| Text `#F4F1EC` on Background `#16161A` | ~16:1 | AAA |
| Dark text on Primary `#E8845A` | ~5.2:1 | AA |

### Implementation in Android (Material Design 3)
Define colors in `res/values/colors.xml` and `res/values-night/colors.xml`. Apply via a custom `Theme.CreativePrompt` in `themes.xml` that extends `Theme.Material3.DayNight`. The system default option maps to Android's `AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM`, with Light and Dark overriding via `AppCompatDelegate.setDefaultNightMode()`. The user's choice is persisted in `SharedPreferences` and applied in `Application.onCreate()` before any Activity launches.

---

## 4. Data Models

### UserPreferences (SharedPreferences)
```
onboardingComplete    : boolean
creativeType          : enum { WRITING, ART, BOTH }

// Writing preferences
writingGenres         : Set<String>
    // Options: "Fantasy", "Sci-Fi", "Horror", "Romance", "Mystery", "Thriller"

// Art preferences
artMediums            : Set<String>
    // Options: "Sketch", "Watercolor", "Digital", "Oil", "Acrylic", "Ink",
    //          "Pixel Art", "Mixed Media"
artSubject            : enum { PEOPLE, LANDSCAPES, BOTH }
    // People & Portraits | Landscapes & Nature | Both

reminderEnabled       : boolean
reminderTimeHour      : int
reminderTimeMinute    : int
useAiWhenAvailable    : boolean
themeMode             : enum { LIGHT, DARK, SYSTEM }   // default: SYSTEM
```

### Prompt (Room Entity)
```java
@Entity(tableName = "prompts")
public class Prompt {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String type;         // "WRITING" or "ART"
    public String genre;        // Writing: e.g. "Fantasy" | Art: medium e.g. "Watercolor"
    public String subject;      // Art only: "PEOPLE", "LANDSCAPES", or "BOTH" — null for writing
    public String content;      // The prompt text
    public String source;       // "AI" or "LOCAL"
    public long timestamp;      // Unix time when generated
    public boolean isFavorite;
}
```

---

## 5. Key Feature Implementation Details

### 5.1 — Hybrid Prompt Generation

**Flow:**
```
User requests prompt
    │
    ├─ useAiWhenAvailable = true AND device is online?
    │       └─ YES → call Claude API
    │                   ├─ Success → display AI prompt, save to Room
    │                   └─ Failure → fall back to local library
    │
    └─ NO → pick from local library based on preferences → display prompt
```

**Claude API call (Retrofit):**
POST to `https://api.anthropic.com/v1/messages`

Example system prompt sent to Claude:
```
You are a creative prompt generator. Generate a single, concise,
inspiring prompt based on the following preferences:

Type: [WRITING / ART]
Writing genre: [e.g. Fantasy]          <- included if type is WRITING
Art medium: [e.g. Watercolor]          <- included if type is ART
Art subject focus: [e.g. Landscapes]   <- included if type is ART

The prompt should be 1-3 sentences, specific enough to spark
imagination but open-ended enough for personal interpretation.
Return ONLY the prompt text, nothing else.
```

**API Key security — proxy architecture (required for release):**

The Claude API key must NEVER be shipped inside the APK. Even obfuscation via ProGuard does not prevent extraction. The production architecture uses a lightweight backend proxy:

```
Android App  ──HTTPS──>  Your Proxy  ──HTTPS──>  Claude API
             (no key)    (holds key as           (key never
                          env variable)           exposed to app)
```

Recommended proxy: **Cloudflare Worker** (free tier, ~30 min setup)
```javascript
// Cloudflare Worker — proxy.js
export default {
  async fetch(request, env) {
    // 1. Validate caller (see Play Integrity below)
    // 2. Forward to Claude with server-side key
    const claudeResponse = await fetch("https://api.anthropic.com/v1/messages", {
      method: "POST",
      headers: {
        "x-api-key": env.CLAUDE_API_KEY,   // stored in Worker env, never in code
        "anthropic-version": "2023-06-01",
        "Content-Type": "application/json"
      },
      body: request.body
    });
    return claudeResponse;
  }
};
```

**Hardening layers (recommended before Play Store release):**

| Layer | What it does | Difficulty |
|---|---|---|
| Cloudflare Worker proxy | Key lives server-side only | Low |
| Rate limiting (CF rules) | Caps requests per IP/device | Low |
| Play Integrity API | Verifies caller is your genuine, unmodified app on a real device | Medium |
| HTTPS certificate pinning | Blocks MITM attacks on proxy calls | Medium |

For development and personal use, `local.properties` + `BuildConfig` is acceptable. Migrate to the proxy before any public distribution.

### 5.2 — Local Prompt Library

Bundled as `assets/prompts.json` inside the APK. Structure:
```json
{
  "writing": {
    "Fantasy":   ["A blacksmith discovers...", "An ancient map leads to...", ...],
    "Sci-Fi":    ["In 2247, the last human...", ...],
    "Horror":    [...],
    "Romance":   [...],
    "Mystery":   [...],
    "Thriller":  [...]
  },
  "art": {
    "Sketch": {
      "People":     ["Sketch a portrait using only 3 lines per feature...", ...],
      "Landscapes": ["Sketch a cityscape at dawn using hatching only...", ...],
      "Both":       ["Sketch a figure standing in a vast open field...", ...]
    },
    "Watercolor": {
      "People":     [...],
      "Landscapes": [...],
      "Both":       [...]
    },
    "Digital":    { "People": [...], "Landscapes": [...], "Both": [...] },
    "Oil":        { "People": [...], "Landscapes": [...], "Both": [...] },
    "Acrylic":    { "People": [...], "Landscapes": [...], "Both": [...] },
    "Ink":        { "People": [...], "Landscapes": [...], "Both": [...] },
    "Pixel Art":  { "People": [...], "Landscapes": [...], "Both": [...] },
    "Mixed Media":{ "People": [...], "Landscapes": [...], "Both": [...] }
  }
}
```

Target: **50+ prompts per genre** for variety. A `PromptLibraryLoader` utility class reads this file once at startup and caches it in memory.

### 5.3 — Daily Reminder Notifications

Uses `WorkManager` with a `PeriodicWorkRequest` (24-hour interval). On trigger, a `DailyReminderWorker` fires a local notification:

> "🎨 Time for your daily creative prompt! Tap to get inspired."

The worker respects the user's set reminder time by calculating the initial delay from the current time to the next scheduled occurrence.

### 5.4 — Repository Pattern

A `PromptRepository` class abstracts prompt sourcing from the UI:
```java
public class PromptRepository {
    public LiveData<Prompt> getPrompt(UserPreferences prefs) {
        // Decides: API vs local, handles fallback, saves to Room
    }
    public LiveData<List<Prompt>> getHistory() { ... }
}
```

ViewModels hold `PromptRepository` and expose `LiveData` to the UI — keeping Activity/Fragment code lean.

---

## 6. Project Structure

```
app/src/main/
├── java/com/yourpackage/creativeprompt/
│   ├── data/
│   │   ├── local/
│   │   │   ├── AppDatabase.java
│   │   │   ├── dao/PromptDao.java
│   │   │   └── entity/Prompt.java
│   │   ├── remote/
│   │   │   ├── ClaudeApiService.java       (Retrofit interface)
│   │   │   ├── ClaudeApiClient.java        (OkHttp + Retrofit setup)
│   │   │   └── model/ClaudeRequest.java / ClaudeResponse.java
│   │   ├── repository/
│   │   │   └── PromptRepository.java
│   │   └── prefs/
│   │       └── UserPreferencesManager.java
│   ├── ui/
│   │   ├── onboarding/
│   │   │   ├── OnboardingActivity.java
│   │   │   └── fragments/  (WelcomeFragment, TypeFragment, WritingGenreFragment,
│   │   │                    ArtMediumFragment, ArtSubjectFragment, ReminderFragment)
│   │   ├── home/
│   │   │   ├── HomeFragment.java
│   │   │   └── HomeViewModel.java
│   │   ├── history/
│   │   │   ├── HistoryFragment.java
│   │   │   └── HistoryViewModel.java
│   │   └── settings/
│   │       ├── SettingsFragment.java
│   │       └── SettingsViewModel.java
│   ├── worker/
│   │   └── DailyReminderWorker.java
│   └── util/
│       ├── PromptLibraryLoader.java
│       ├── NetworkUtils.java
│       └── AppContainer.java               (manual DI)
├── assets/
│   └── prompts.json
└── res/
    ├── layout/
    ├── drawable/
    └── values/  (themes, colors, strings)
```

---

## 7. Implementation Phases

### Phase 1 — Foundation (Week 1)
- Project setup, Gradle dependencies, package structure
- Room database + DAO + entity
- `UserPreferencesManager` (SharedPreferences wrapper)
- `prompts.json` first draft + `PromptLibraryLoader`
- Basic `MainActivity` shell with bottom nav

### Phase 2 — Onboarding + Preferences (Week 2)
- Full onboarding flow (4 fragments, ViewPager2)
- `SettingsFragment` mirroring onboarding options
- First-launch detection and redirect logic

### Phase 3 — Prompt Generation (Week 3)
- `ClaudeApiService` + Retrofit setup
- `PromptRepository` with hybrid logic (API → fallback)
- `HomeFragment` + `HomeViewModel` wiring
- Prompt card UI (Material Card, type/genre badges)

### Phase 4 — History + Notifications (Week 4)
- `HistoryFragment` with RecyclerView + Room query
- `DailyReminderWorker` + WorkManager scheduling
- Notification channel setup, reminder time preference

### Phase 5 — Polish & Testing (Week 5)
- UI polish: animations, loading states, error handling
- Edge case handling: no internet, empty library match, API errors
- Unit tests for `PromptRepository` logic
- Manual testing on multiple screen sizes / Android versions
- ProGuard rules, release build setup

---

## 8. Key Considerations & Potential Challenges

**API Key Distribution**
Embedding the Claude API key in the APK is a critical security risk — tools like `apktool` and `jadx` can decompile any APK and extract strings in minutes. The production solution is a Cloudflare Worker (or equivalent serverless proxy) that holds the key as an environment variable. The Android app calls your proxy; your proxy calls Claude. See Section 5.1 for the full architecture. For development only, `local.properties` + `BuildConfig` is acceptable as it at least keeps the key out of source control.

**Prompt Repetition**
Track which local prompts have been shown (store shown IDs in Room or SharedPreferences) and avoid repeating until the library is exhausted, then reset. For AI prompts, include recent prompt text in the API call context to explicitly ask for something different.

**Offline-First UX**
The app should never show a blank state. If the user's genre preferences don't match any local prompts (unusual edge case), show a random prompt and note that it may not match their preferences exactly.

**WorkManager Reliability**
Android battery optimization (Doze mode, app standby) can delay WorkManager tasks. For reminders, this is acceptable — a notification arriving 15 minutes late is fine. Document this as expected behavior.

**Future Extension Points**
The MVVM + Repository architecture makes it straightforward to later add: user accounts and cloud sync, favorites/collections, sharing prompts, streak/gamification tracking, or additional creative categories (music, photography, etc.).
