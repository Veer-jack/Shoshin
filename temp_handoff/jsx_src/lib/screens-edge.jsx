// ============================================================
// Shoshin — Edge states
//   Day 1 (empty) · Broken streak · Offline ·
//   Alarm permission denied · Returning user · Wrong answer
// Requires: icons.jsx, components.jsx
// ============================================================

// Generic centered edge layout
function EdgeLayout({ platform, dark, motif, kicker, kickerVariant, title, body, children }) {
  return (
    <div className={`sh-screen${dark ? " dark" : ""}`} style={{ paddingTop: shTopInset(platform) }}>
      <div className="sh-body sh-pad" style={{ display: "flex", flexDirection: "column" }}>
        <div style={{ flex: 1, display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", textAlign: "center", gap: 6 }}>
          {motif}
          {kicker && <div className={`sh-kicker ${kickerVariant || "accent"}`} style={{ marginTop: 8, width: "100%" }}>{kicker}</div>}
          <h1 className="sh-title" style={{ fontSize: 30, width: "100%", color: dark ? "var(--sh-night-text)" : "var(--sh-ink)", marginTop: 4 }}>{title}</h1>
          {body && <p className="sh-body-text" style={{ fontSize: 15, maxWidth: 300, marginTop: 8, color: dark ? "var(--sh-night-muted)" : "var(--sh-fog)" }}>{body}</p>}
        </div>
        <div style={{ paddingBottom: 28 }}>{children}</div>
      </div>
    </div>
  );
}

// ============================================================
// 19 · EMPTY / DAY 1
// ============================================================
function Day1Screen({ platform, onBegin }) {
  return (
    <EdgeLayout platform={platform}
      motif={<Enso size={150} color="var(--sh-ink)" strokeW={7} />}
      kicker="Day one" title="A clean page" kickerVariant="accent"
      body="No streak yet, no history — just tomorrow morning, and the first small step across the bridge.">
      <Button variant="accent" onClick={onBegin}>Set your first wake</Button>
      <p className="sh-label" style={{ textAlign: "center", marginTop: 16, color: "var(--sh-fog-2)" }}>Every practice begins once.</p>
    </EdgeLayout>
  );
}

// ============================================================
// 20 · BROKEN STREAK
// ============================================================
function BrokenStreakScreen({ platform, onRestart }) {
  return (
    <div className="sh-screen" style={{ paddingTop: shTopInset(platform) }}>
      <div className="sh-body sh-pad" style={{ display: "flex", flexDirection: "column" }}>
        <div style={{ flex: 1, display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", textAlign: "center" }}>
          {/* broken chain motif */}
          <svg width="120" height="80" viewBox="0 0 120 80" fill="none" style={{ marginBottom: 8 }}>
            <circle cx="30" cy="40" r="13" stroke="var(--sh-matcha)" strokeWidth="2.5" />
            <circle cx="58" cy="40" r="13" stroke="var(--sh-line-2)" strokeWidth="2.5" strokeDasharray="3 4" />
            <circle cx="90" cy="40" r="13" stroke="var(--sh-line-2)" strokeWidth="2.5" />
            <path d="M60 26 L56 54" stroke="var(--sh-vermillion)" strokeWidth="2.5" strokeLinecap="round" />
          </svg>
          <div className="sh-kicker" style={{ marginTop: 8, width: "100%" }}>The chain rested</div>
          <h1 className="sh-title" style={{ fontSize: 30, width: "100%", marginTop: 4 }}>A miss is not<br />a failure</h1>
          <p className="sh-body-text" style={{ fontSize: 15, maxWidth: 300, marginTop: 8 }}>You held 14 mornings. That practice is yours to keep. Shoshin means beginning again — without judgement.</p>

          {/* what's preserved */}
          <div className="sh-card" style={{ padding: 18, marginTop: 24, width: "100%", display: "flex", justifyContent: "space-around" }}>
            <Stat value="14" label="Best held" align="center" color="var(--sh-matcha)" />
            <div style={{ width: 1, background: "var(--sh-line)" }}></div>
            <Stat value="148" label="Total kept" align="center" />
            <div style={{ width: 1, background: "var(--sh-line)" }}></div>
            <Stat value="86" unit="%" label="All-time" align="center" />
          </div>
        </div>
        <div style={{ paddingBottom: 28 }}>
          <Button variant="accent" onClick={onRestart}>Begin again</Button>
          <p className="sh-label" style={{ textAlign: "center", marginTop: 16, color: "var(--sh-fog-2)" }}>Tomorrow is day one of the next chain.</p>
        </div>
      </div>
    </div>
  );
}

// ============================================================
// 21 · OFFLINE
// ============================================================
function OfflineScreen({ platform, onRetry }) {
  return (
    <div className="sh-screen" style={{ paddingTop: shTopInset(platform) }}>
      <div className="sh-body sh-pad" style={{ display: "flex", flexDirection: "column" }}>
        {/* banner */}
        <div style={{ display: "flex", alignItems: "center", gap: 10, padding: "12px 16px", borderRadius: 12, background: "var(--sh-paper-2)", marginTop: 16, marginBottom: 22 }}>
          <span style={{ width: 8, height: 8, borderRadius: "50%", background: "var(--sh-fog)", flexShrink: 0 }}></span>
          <span className="sh-label" style={{ fontWeight: 600, flex: 1 }}>You're offline — showing your last sync</span>
          <button onClick={onRetry} style={{ background: "none", border: "none", cursor: "pointer", fontFamily: "var(--sh-sans)", fontWeight: 600, fontSize: 13, color: "var(--sh-vermillion)" }}>Retry</button>
        </div>

        {/* cached content (dimmed) */}
        <div style={{ opacity: 0.6, pointerEvents: "none", flex: 1 }}>
          <div className="sh-kicker" style={{ marginBottom: 8 }}>Cached · Saturday</div>
          <h1 className="sh-title" style={{ fontSize: 26, marginBottom: 18 }}>Your practice</h1>
          <div className="sh-card" style={{ padding: 22, marginBottom: 14, display: "flex", gap: 20, alignItems: "center" }}>
            <Ring value={86} size={84} stroke={8} label="86" sub="Saved" />
            <div>
              <div className="sh-num" style={{ fontSize: 22 }}>14</div>
              <div className="sh-kicker" style={{ fontSize: 9.5, marginTop: 2 }}>Mornings kept</div>
            </div>
          </div>
          <div className="sh-card" style={{ padding: "8px 18px" }}>
            <Checkpoint index={1} icon="brain" label="Mind awake" state="done" time="05:33" />
            <Checkpoint index={2} icon="droplet" label="Freshen up" state="done" time="05:36" />
          </div>
        </div>

        <div style={{ paddingBottom: 28, paddingTop: 16 }}>
          <Button variant="ghost" icon="pulse" onClick={onRetry}>Try to reconnect</Button>
          <p className="sh-label" style={{ textAlign: "center", marginTop: 16, color: "var(--sh-fog-2)" }}>Your mornings sync the moment you're back.</p>
        </div>
      </div>
    </div>
  );
}

// ============================================================
// 22 · ALARM PERMISSION DENIED
// ============================================================
function PermDeniedScreen({ platform, onOpen }) {
  return (
    <div className="sh-screen" style={{ paddingTop: shTopInset(platform) }}>
      <div className="sh-body sh-pad" style={{ display: "flex", flexDirection: "column" }}>
        <div style={{ flex: 1, display: "flex", flexDirection: "column", justifyContent: "center" }}>
          <div style={{ width: 60, height: 60, borderRadius: 17, background: "rgba(200,75,49,0.10)", display: "flex", alignItems: "center", justifyContent: "center", marginBottom: 22 }}>
            <Icon name="bell" size={28} color="var(--sh-vermillion)" />
          </div>
          <div className="sh-kicker accent" style={{ marginBottom: 12 }}>Alarms are off</div>
          <h1 className="sh-title" style={{ fontSize: 30, marginBottom: 12 }}>We can't wake<br />you yet</h1>
          <p className="sh-body-text" style={{ fontSize: 15, marginBottom: 26 }}>Shoshin needs alarm &amp; notification permission to reach you at dawn. Without it, your morning won't begin.</p>

          {/* steps */}
          <div className="sh-card" style={{ padding: "6px 18px" }}>
            {[["settings", "Open Settings", "iOS Settings › Shoshin"], ["bell", "Allow Notifications", "Turn on, then enable Critical Alerts"], ["clock", "Allow Alarms & Timers", "So we can sound at 5:30 AM"]].map((s, i) => (
              <div key={i} className="sh-row">
                <div style={{ width: 26, height: 26, borderRadius: "50%", background: "var(--sh-ink)", color: "var(--sh-paper)", display: "flex", alignItems: "center", justifyContent: "center", fontFamily: "var(--sh-sans)", fontWeight: 600, fontSize: 13, flexShrink: 0 }}>{i + 1}</div>
                <div className="sh-row-main">
                  <div className="sh-row-title">{s[1]}</div>
                  <div className="sh-row-sub">{s[2]}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
        <div style={{ paddingBottom: 28 }}>
          <Button variant="accent" iconRight="arrowR" onClick={onOpen}>Open Settings</Button>
        </div>
      </div>
    </div>
  );
}

// ============================================================
// 23 · RETURNING USER
// ============================================================
function ReturningScreen({ platform, onResume }) {
  return (
    <div className="sh-screen" style={{ paddingTop: shTopInset(platform) }}>
      <div className="sh-body sh-pad" style={{ display: "flex", flexDirection: "column" }}>
        <div style={{ flex: 1, display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", textAlign: "center" }}>
          <div style={{ width: 78, height: 78, borderRadius: "50%", background: "var(--sh-sand)", display: "flex", alignItems: "center", justifyContent: "center", marginBottom: 20 }}>
            <span style={{ fontFamily: "var(--sh-display)", fontWeight: 600, fontSize: 34, color: "var(--sh-ink)" }}>A</span>
          </div>
          <div className="sh-kicker accent">Welcome back</div>
          <h1 className="sh-title" style={{ fontSize: 32, width: "100%", marginTop: 6 }}>Good to see you,<br />Arjun</h1>
          <p className="sh-body-text" style={{ fontSize: 15, maxWidth: 300, marginTop: 10 }}>It's been 3 days. Your 71-day practice paused at day 38 — and it's waiting exactly where you left it.</p>

          <div className="sh-card" style={{ padding: 18, marginTop: 24, width: "100%", display: "flex", alignItems: "center", gap: 16 }}>
            <Ring value={Math.round(38 / 71 * 100)} size={56} stroke={6} color="var(--sh-vermillion)" label="" />
            <div style={{ flex: 1, textAlign: "left" }}>
              <div className="sh-h2" style={{ fontSize: 15 }}>71-Day Discipline</div>
              <div className="sh-row-sub">Resume at day 38</div>
            </div>
            <Pill variant="matcha">Held</Pill>
          </div>
        </div>
        <div style={{ paddingBottom: 28 }}>
          <Button variant="accent" onClick={onResume}>Resume practice</Button>
          <p className="sh-label" style={{ textAlign: "center", marginTop: 16, color: "var(--sh-fog-2)" }}>Beginner's mind. Pick up gently.</p>
        </div>
      </div>
    </div>
  );
}

// ============================================================
// 24 · WRONG ANSWER RECOVERY (dark)
// ============================================================
function WrongAnswerScreen({ platform, onRetry }) {
  return (
    <div className="sh-screen dark" style={{ paddingTop: shTopInset(platform) }}>
      <div className="sh-body" style={{ display: "flex", flexDirection: "column", padding: "0 24px 24px", textAlign: "center" }}>
        <div style={{ textAlign: "center", marginTop: 18 }}>
          <div className="sh-num" style={{ fontSize: 40, color: "var(--sh-night-text)" }}>05:31</div>
        </div>

        <div style={{ flex: 1, display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", gap: 18 }}>
          <div style={{ width: 64, height: 64, borderRadius: "50%", background: "rgba(200,75,49,0.14)", display: "flex", alignItems: "center", justifyContent: "center" }}>
            <Icon name="info" size={30} color="var(--sh-vermillion)" />
          </div>
          <div style={{ width: "100%" }}>
            <div className="sh-kicker accent">Not quite</div>
            <h1 className="sh-title" style={{ fontSize: 28, color: "var(--sh-night-text)", marginTop: 8 }}>Breathe. Look again.</h1>
            <p className="sh-body-text" style={{ fontSize: 14.5, color: "var(--sh-night-muted)", maxWidth: 280, marginTop: 10 }}>A wrong answer wakes the mind faster than a right one. No penalty — just try once more.</p>
          </div>

          {/* attempts */}
          <div style={{ display: "flex", gap: 10, alignItems: "center" }}>
            {[0, 1, 2].map((i) => (
              <div key={i} style={{ display: "flex", alignItems: "center", gap: 6 }}>
                <span style={{ width: 11, height: 11, borderRadius: "50%", background: i < 1 ? "var(--sh-vermillion)" : "rgba(255,255,255,0.18)" }}></span>
              </div>
            ))}
            <span className="sh-label" style={{ color: "var(--sh-night-muted)", marginLeft: 6 }}>1 of 3 attempts used</span>
          </div>
        </div>

        <Button variant="accent" onClick={onRetry}>Try again</Button>
        <div style={{ display: "flex", alignItems: "center", justifyContent: "center", gap: 8, marginTop: 18 }}>
          <Icon name="lock" size={15} color="var(--sh-night-muted)" />
          <span className="sh-label" style={{ color: "var(--sh-night-muted)" }}>Snooze rests until your mind wakes.</span>
        </div>
      </div>
    </div>
  );
}

Object.assign(window, {
  EdgeLayout, Day1Screen, BrokenStreakScreen, OfflineScreen,
  PermDeniedScreen, ReturningScreen, WrongAnswerScreen,
});
