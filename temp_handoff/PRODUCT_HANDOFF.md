# Shoshin (初心) — Complete Product Handoff
**"Beginner's mind. Every morning."** — Full design system, all 41 screens, motion spec, and state coverage (loading / success / error / empty) for engineering implementation.

---

## 1. Full Screen Inventory (41 screens)

### Onboarding (7)
| # | Screen | File | Notes |
|---|--------|------|-------|
| 01 | Splash | screens-hero.jsx | Dark. Entry point. |
| 02 | Auth | screens-onboard.jsx | Google + Apple OAuth, Phone/Email tabs |
| 03 | Phone OTP | screens-onboard.jsx | 6-digit, auto-verify, resend countdown |
| 03b | Email OTP | screens-onboard.jsx | Mail icon badge variant of OTP |
| 04 | Onboarding (3 slides) | screens-onboard.jsx | Swipeable/paged, skip anytime |
| 05 | Permissions | screens-onboard.jsx | Notifications + Camera, honest framing |
| 06 | Goal Selection | screens-onboard.jsx | 4 goal cards |
| 07 | Routine Template | screens-onboard.jsx | Template preview per goal |

### Morning Flow (7)
| # | Screen | File | Notes |
|---|--------|------|-------|
| 08 | Home Dashboard | screens-hero.jsx | Next-wake hero card, bridge preview |
| 09 | Alarm Setup | screens-morning.jsx | Time, repeat days, difficulty, sound |
| 09b | Sound Picker | screens-extra.jsx | 7 sounds, volume slider |
| 10 | Morning Activation | screens-hero.jsx | Dark. Math-gate challenge, no back nav |
| 11 | Camera Verification | screens-morning.jsx | Dark. Live viewfinder + shutter |
| 12 | Checkpoint Completion | screens-morning.jsx | Step-through, per-checkpoint hero card |
| 12b | Morning Complete | screens-extra.jsx | Celebration + summary stats |

### Progress (4)
| # | Screen | File | Notes |
|---|--------|------|-------|
| 13 | Consistency Dashboard | screens-progress.jsx | Ring + week bars + 2×2 stats |
| 13b | History / Calendar | screens-extra.jsx | Month grid, day detail |
| 14 | 21-Day Challenge | screens-progress.jsx | Streak grid, foundation phase |
| 15 | 71-Day Discipline | screens-progress.jsx | Dark. 3-phase progress |

### Account & Pro (5)
| # | Screen | File | Notes |
|---|--------|------|-------|
| 16 | Profile | screens-account.jsx | Stats, active challenge, badge preview |
| 17 | Settings | screens-account.jsx | Grouped rows: account/practice/notif/app/help |
| 18 | Routine Editor | screens-account.jsx | Drag-reorder checkpoints |
| 18b | Groups | screens-extra.jsx | Pod wake-board + member list |
| 18c | Shoshin Pro (Paywall) | screens-extra.jsx | Dark. Feature list + plan toggle |

### Edge States (6)
| # | Screen | File | Notes |
|---|--------|------|-------|
| 19 | Day One (empty) | screens-edge.jsx | No history state |
| 20 | Broken Streak | screens-edge.jsx | Failure-recovery framing |
| 21 | Offline | screens-edge.jsx | Cached content, dimmed, retry |
| 22 | Alarm Permission Denied | screens-edge.jsx | Numbered fix steps |
| 23 | Returning User | screens-edge.jsx | Win-back after absence |
| 24 | Wrong Answer | screens-edge.jsx | Dark. Attempt counter, no penalty |

### Support & Account (4)
| # | Screen | File | Notes |
|---|--------|------|-------|
| 25 | Invite / Referral | screens-support.jsx | Code, progress bar, share/email CTA |
| 26 | Help & Support | screens-support.jsx | Search, contact rows, FAQ accordion |
| 27 | Notifications | screens-support.jsx | Unread dot, grouped by icon/time |
| 28 | Privacy & Data | screens-support.jsx | Export + delete-account confirm modal |

### Social & Growth (6)
| # | Screen | File | Notes |
|---|--------|------|-------|
| 29 | Share | screens-social.jsx | 3 card styles, share-target row |
| 30 | Badge Unlock | screens-social.jsx | Dark. Celebration medallion |
| 31 | Streak Details | screens-social.jsx | Monthly trend bars |
| 32 | Badges (grid) | screens-social.jsx | 3-col grid, earned/locked |
| 33 | Badge Detail | screens-social.jsx | Single badge, share CTA if earned |
| 34 | Personal Stats | screens-social.jsx | 2×2 + avg times + path breakdown |

### Friends & Groups (6)
| # | Screen | File | Notes |
|---|--------|------|-------|
| 35 | Friend Profile | screens-friends.jsx | Streak stats, shared circles |
| 36 | All Friends | screens-friends.jsx | Search + list with streak |
| 37 | Friend Invite | screens-friends.jsx | Search + suggested contacts |
| 38 | Group Invite | screens-friends.jsx | Multi-select candidates |
| 39 | Group Preview | screens-friends.jsx | Pre-join preview, avatar stack |
| 40 | Group Leaderboard | screens-friends.jsx | Podium top-3 + ranked list |

**Total: 41 screens** across 9 flow groups.

---

## 2. Design Tokens Reference

See `lib/tokens.css` for canonical values. Summary:

**Color**: Sumi Ink `#1C1C1E` (primary) · Washi Paper `#FAF9F6` (bg) · Vermillion `#C84B31` (single accent per screen) · Matcha `#4A7C59` (success) · Sand `#E8E4DC` (secondary fill) · Fog `#8A8580` / Fog-2 `#B0ABA2` (muted text) · Night `#0F0F0F` / Night-2 `#1A1A1A` (dark screens).

**Type**: Cormorant Garamond 600 (display/titles) · DM Sans (all UI, 400/500/600/700) · Noto Serif JP 500 (kanji accent, vermillion).

**Radius**: 14px buttons · 20px cards · 999px pills.

**Icons**: 24×24 grid, 1.5px stroke, round caps/joins, no fill. 30 icons in `lib/icons.jsx` including newly added: `share`, `mail`, `gift`, `trash`, `download`, `search`, `help`, `crown`, `arrowUp`, `arrowDown`, `grid`, `close`.

---

## 3. Motion & Animation Specification

Shoshin's motion language: **quiet, deliberate, never bouncy.** Every transition should feel like a considered breath, not a flourish. Default easing `cubic-bezier(.2,.8,.2,1)` (ease-out, no overshoot) unless noted.

### 3.1 Screen-to-screen transitions
| Transition type | Used for | Timing | Easing |
|---|---|---|---|
| Slide (push/pop) | Linear stack nav (Auth→OTP, Settings→Referral, etc.) | 320ms | ease-out / ease-in on pop |
| Fade | Major context switch (Splash→Auth, OTP→Onboarding, Onboarding→Main, Checkpoint→Complete) | 300–600ms | ease |
| Slide up (modal) | Paywall, Group Preview (decline = slide down) | 400ms in / 300ms out | ease-out |
| Instant (tab switch) | Bottom nav tab change | 0ms (content swap only) | — |

### 3.2 Component micro-animations
- **Ring** (`Ring` component): stroke-dashoffset animates from 0→value on mount, **900ms**, `cubic-bezier(.2,.8,.2,1)`. Never re-animate on re-render — only on value change.
- **WeekBars**: bar height transitions **600ms** ease-out on data change (e.g. after completing today's morning).
- **StreakGrid / checkpoint chain**: each cell that flips to "done" pops with a **150ms scale(1→1.08→1)** + check-icon fade-in **120ms**. Stagger by 40ms per cell if multiple complete at once (e.g. loading history).
- **Checkpoint row (Checkpoint component)**: node background transitions **200ms** ease when state changes pending→active→done. Active state adds outer glow ring (box-shadow) that fades in over **150ms**.
- **Toggle**: thumb `translateX` **200ms** `cubic-bezier(.3,.7,.4,1)`, track color crossfade **200ms**.
- **Button press**: `scale(0.985)` **120ms** ease on `:active`, springs back on release.
- **Keypad key press**: `scale(0.95)` on mousedown/touchstart, back to 1 on release — **100ms** linear (fast, tactile).
- **FAB (tab bar center)**: `scale(0.94)` on press.
- **Segmented control**: selected pill background slides via layout transition **150ms** ease.

### 3.3 Celebration moments (high-emphasis)
- **Morning Complete** (screen 12b): enso ring draws in **900ms**, check-circle scales in **300ms delay 600ms** (`scale(0)→scale(1)`, slight overshoot `cubic-bezier(.34,1.56,.64,1)` — the ONE place a small overshoot is allowed), streak pill "+1" fades/slides up **300ms delay 900ms**.
- **Badge Unlock** (screen 30): dashed ring rotates slowly (**20s linear infinite**, decorative only — pause under reduced motion), medallion scales in **400ms** with the same overshoot easing, kicker/title/body stagger in **200ms** each, 100ms apart.
- Both celebration screens: consider a **subtle confetti/particle burst** (8–12 small vermillion/matcha dots, radiate + fade, **800ms**, one-shot) behind the medallion — optional polish, must respect `prefers-reduced-motion`.

### 3.4 Reduced motion
Wrap all non-essential animation (ring draw, bar grow, confetti, rotating dashed ring) in `@media (prefers-reduced-motion: no-preference)`. Under reduced motion: show end-states immediately, keep only opacity crossfades ≤150ms.

---

## 4. State Coverage — Loading / Success / Error / Empty

Every async-feeling flow needs all four states. Below maps each to an existing screen (reuse) or a net-new state to build in code (not separately mocked, since they're transient).

### 4.1 Auth & OTP
- **Loading**: OTP auto-submit shows a brief inline spinner in place of the arrow-right icon on the OK key (or a 3-dot pulse under the boxes) for ~400–800ms while verifying.
- **Success**: auto-advances to Onboarding (fade, see §3.1) — no explicit "success" screen needed, the *transition itself* is the success signal.
- **Error**: invalid/expired code → OTP boxes get `1.5px` red-tinted border (`--sh-vermillion`) + shake (`translateX ±4px, 3 cycles, 300ms`) + inline label below boxes: *"That code didn't match. Try again or resend."* Do not clear the resend timer.
- **Empty**: N/A (form always has a default state).

### 4.2 Morning Activation (math gate)
- **Loading**: N/A — pure client-side math check, instant.
- **Success**: advances to Camera/Checkpoint (fade 300ms).
- **Error**: **Wrong Answer screen (24)** — already built. Reuse verbatim; increments attempt-dot counter.
- **Empty**: N/A.

### 4.3 Camera Verification (checkpoint photo)
- **Loading**: after shutter tap, freeze-frame + a thin **vermillion progress bar** sweeps across the bottom edge of the viewfinder (**600–900ms**, indeterminate until upload confirms) while the photo is being saved/uploaded.
- **Success**: viewfinder briefly flashes white (**100ms**) then screen pops back to Checkpoint with that checkpoint now marked `done` (see checkpoint pop animation §3.2).
- **Error**: if capture/upload fails — bottom overlay caption swaps to vermillion background, text: *"Couldn't save that photo. Try again."* with a retry shutter tap. Never block progress entirely — offer "Skip · −1 proof" as escape hatch (already in UI).
- **Empty**: N/A (camera always has a live feed or permission-denied fallback — see §4.7).

### 4.4 Checkpoint Completion → Morning Complete
- **Loading**: N/A, purely local state advance.
- **Success**: **Morning Complete (12b)** — already built, is the success state for the whole morning flow.
- **Error**: N/A.
- **Empty**: **Day 1 (19)** already covers "no history yet" for a first-time user reaching Home before any morning is logged.

### 4.5 Referral / Invite send
- **Loading**: "Share invite link" / "Invite by email" buttons show a brief inline spinner replacing the icon for ~500ms while the OS share sheet or email intent opens.
- **Success**: toast/snackbar (bottom, 3s, matcha check icon): *"Invite sent."* Progress bar on Referral screen increments live if a redemption happens.
- **Error**: toast (vermillion): *"Couldn't open share sheet. Try again."*
- **Empty**: Referral screen's "Invited so far 0/5" state — same layout, progress bar at 0%, no special empty illustration needed (it's already minimal).

### 4.6 Notifications
- **Loading**: skeleton rows — 4 pill-shaped placeholders (icon-chip circle + two text lines) at 40% opacity, subtle shimmer optional.
- **Success**: populated list (already built, screen 27).
- **Error**: inline banner at top of list: *"Couldn't refresh notifications."* + Retry text button — same visual pattern as **Offline (21)** banner.
- **Empty**: centered state reusing `EdgeLayout` pattern: bell icon motif, kicker "All caught up", title "Nothing new", body "We'll let you know when your circle rises or a badge is earned."

### 4.7 Permissions (camera / notifications denied mid-flow)
- **Error / denied**: **Alarm Permission Denied (22)** already covers notifications/alarm. For camera specifically, reuse the same layout with icon `camera`, title "We can't verify yet", steps pointing to Settings → Camera.
- **Loading**: OS permission dialogs are system-owned, no custom loading needed.

### 4.8 Offline / network
- **Global pattern**: **Offline (21)** — dimmed cached content + banner + retry. Apply this same banner pattern (not full screen) to Consistency, Groups, Notifications, Leaderboard whenever a background sync fails — don't block the whole screen, dim only data that's stale.

### 4.9 Group Invite / Join
- **Loading**: "Send invites" button shows inline spinner ~500ms.
- **Success**: toast: *"Invites sent."* Return to Groups.
- **Error**: toast (vermillion): *"Some invites didn't send. Try again."*
- **Empty (All Friends)**: if a user has zero friends yet — reuse `EdgeLayout`: icon `users`, kicker "Your circle is quiet", title "No friends yet", body "Invite someone who needs a reason to rise.", CTA button → Friend Invite.
- **Empty (Group Leaderboard)**: N/A — a group always has ≥1 member (the user).

### 4.10 Data Export
- **Loading**: after tapping "Export my data" — row shows inline spinner + label changes to "Preparing your export…" for the request duration.
- **Success**: toast: *"Export ready — check your email."* (or native share sheet with the file, if generated client-side/instantly).
- **Error**: toast (vermillion): *"Export failed. Try again in a moment."*

### 4.11 Account Deletion
- Confirmation modal already built (screen 28's inline modal). Add:
- **Loading**: modal's "Delete permanently" button shows inline spinner + disables both buttons while the request is in flight.
- **Success**: navigate to Splash (session cleared) — no extra screen needed.
- **Error**: modal stays open, body text swaps to vermillion: *"Something went wrong. Your account was not deleted — try again."*

---

## 5. Navigation — New Screens Added to Connection Map

Extends `android_handoff/navigation/SCREEN_CONNECTIONS.md` (Kotlin agent should merge these in):

```
SETTINGS
  → [Tap "Invite the circle"]   → REFERRAL          [slide right]
  → [Tap "Help & support"]      → SUPPORT           [slide right]
  → [Tap "Privacy & data"]      → PRIVACY           [slide right]

HOME
  → [Tap bell icon in header, if present] → NOTIFICATIONS  [slide right]

PROFILE
  → [Tap "See all" on badges]   → BADGES            [slide right]
  → [Tap "View full stats"]     → PERSONAL_STATS    [slide right]

BADGES
  → [Tap any badge]             → BADGE_DETAIL(badge)  [slide right]
  → [Badge newly earned, anywhere in app] → BADGE_UNLOCK(badge)  [fade 400ms, full screen, dismissable]

BADGE_DETAIL / BADGE_UNLOCK / STREAK_DETAILS / CONSISTENCY / PERSONAL_STATS
  → [Tap "Share"]                → SHARE(context)     [fade or slide right]

CONSISTENCY
  → [Tap streak or "Streak details" affordance] → STREAK_DETAILS  [slide right]

GROUPS
  → [Tap "Invite someone to the circle"] → GROUP_INVITE   [slide right]
  → [Tap "View leaderboard"]             → GROUP_LEADERBOARD [slide right]
  → [Tap a member row]                   → FRIEND_PROFILE(friend) [slide right]
  → [Deep link / accept invite, external] → GROUP_PREVIEW(group) [modal fade]

GROUP_INVITE
  → [Tap "Send invites"]         → GROUPS (pop, toast success)

GROUP_PREVIEW
  → [Tap "Join the circle"]      → GROUPS (pop, popUpTo GROUP_PREVIEW inclusive)
  → [Tap close / "Not now"]      → GROUPS (pop)

FRIEND_PROFILE
  → [Tap "Follow"]                → (inline state change, stays on screen)

ALL_FRIENDS  (reached from Groups "See all" if added, or Friend Invite back-nav)
  → [Tap a friend row]            → FRIEND_PROFILE(friend)  [slide right]
  → [Tap + icon]                  → FRIEND_INVITE           [slide right]

FRIEND_INVITE
  → [Tap "Invite" on suggestion]  → (toast success, stays on screen)
  → [Tap "Share invite link"]     → (OS share sheet)

REFERRAL
  → [Tap "Share invite link"]     → (OS share sheet)
  → [Tap "Invite by email"]       → (email compose intent)

SUPPORT
  → [Tap FAQ row]                 → (inline accordion expand, no navigation)
  → [Tap "Message support"]       → (email/chat intent)

PRIVACY
  → [Tap "Export my data"]        → (background job + toast)
  → [Tap "Delete account", confirm] → SPLASH (session cleared, full stack popped)
```

---

## 6. What's in the download

```
/lib/            — icons.jsx, components.jsx, tokens.css, all screens-*.jsx (9 files, 41 screens)
/frames/         — ios-frame.jsx, android-frame.jsx, tweaks-panel.jsx, design-canvas.jsx
Shoshin Prototype.html   — full clickable prototype, all 41 screens, iOS/Android toggle
Shoshin Gallery.html     — static grid of every screen for quick visual QA
Shoshin Design System.html — token + component reference
android_handoff/         — Kotlin/Compose starter (colors, type, theme, components, nav graph)
PRODUCT_HANDOFF.md        — this file
```

**For the coding agent**: treat `lib/*.jsx` as the single source of truth for copy, layout, and visual spec per screen — port each React component to native (Compose/SwiftUI) 1:1, using `android_handoff/` as the Kotlin starting point and this doc for motion + state coverage that isn't visible in a static screen.
