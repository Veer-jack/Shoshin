// ============================================================
// Shoshin — Account screens
//   Profile · Settings · Routine Editor
// Requires: icons.jsx, components.jsx, screens-hero.jsx (SH_TEMPLATES)
// ============================================================

const SH_BADGES = [
  { icon: "flame", label: "14 kept", earned: true },
  { icon: "trophy", label: "Best 31", earned: true },
  { icon: "shield", label: "Zero misses", earned: true },
  { icon: "sun", label: "Early riser", earned: true },
  { icon: "target", label: "100 days", earned: false },
  { icon: "book", label: "Scholar", earned: false },
];

// ============================================================
// 16 · PROFILE
// ============================================================
function ProfileScreen({ platform, onNav, onSettings, onBadges, onStats, onNotifications }) {
  return (
    <div className="sh-screen">
      <div className="sh-body sh-pad" style={{ paddingTop: shTopInset(platform) }}>
        <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginTop: 16, marginBottom: 22 }}>
          <h1 className="sh-title" style={{ fontSize: 28 }}>You</h1>
          <div style={{ display: "flex", alignItems: "center", gap: 18 }}>
            <button onClick={onNotifications} style={{ background: "none", border: "none", cursor: "pointer", padding: 0 }}>
              <Icon name="bell" size={22} color="var(--sh-ink)" />
            </button>
            <button onClick={onSettings} style={{ background: "none", border: "none", cursor: "pointer", padding: 0 }}>
              <Icon name="settings" size={24} color="var(--sh-ink)" />
            </button>
          </div>
        </div>

        {/* profile card */}
        <div className="sh-card" style={{ padding: 24, marginBottom: 14, textAlign: "center" }}>
          <div style={{ width: 78, height: 78, borderRadius: "50%", background: "var(--sh-sand)", display: "flex", alignItems: "center", justifyContent: "center", margin: "0 auto 14px" }}>
            <span style={{ fontFamily: "var(--sh-display)", fontWeight: 600, fontSize: 34, color: "var(--sh-ink)" }}>A</span>
          </div>
          <h2 className="sh-title" style={{ fontSize: 24, marginBottom: 8 }}>Arjun Mehta</h2>
          <div style={{ display: "flex", gap: 8, justifyContent: "center", marginBottom: 20 }}>
            <Pill variant="ink">Tier II · Disciplined</Pill>
            <Pill variant="outline">Morning walker</Pill>
          </div>
          <hr className="sh-hr" style={{ marginBottom: 18 }} />
          <div style={{ display: "flex", justifyContent: "space-around" }}>
            <Stat value="148" label="Mornings" align="center" />
            <Stat value="14" label="Current" align="center" color="var(--sh-vermillion)" />
            <Stat value="86" unit="%" label="Consistency" align="center" />
          </div>
        </div>

        {/* active challenge */}
        <div className="sh-card" style={{ padding: 20, marginBottom: 22, display: "flex", alignItems: "center", gap: 18 }}>
          <Ring value={Math.round(38 / 71 * 100)} size={64} stroke={7} color="var(--sh-vermillion)" label="" />
          <div style={{ flex: 1 }}>
            <div className="sh-kicker accent" style={{ marginBottom: 4 }}>In progress</div>
            <div className="sh-h2">71-Day Discipline</div>
            <div className="sh-row-sub" style={{ marginTop: 2 }}>Day 38 · Reinforcement phase</div>
          </div>
          <Icon name="chevR" size={18} color="var(--sh-fog-2)" />
        </div>

        {/* badges */}
        <SectionTitle kicker="EARNED" title="Marks of practice" action="See all" onAction={onBadges} />
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 12, marginBottom: 14 }}>
          {SH_BADGES.map((b, i) => (
            <div key={i} className="sh-card" onClick={onBadges} style={{ padding: "20px 8px", textAlign: "center", opacity: b.earned ? 1 : 0.45, cursor: "pointer" }}>
              <div style={{ width: 48, height: 48, borderRadius: "50%", margin: "0 auto 10px", display: "flex", alignItems: "center", justifyContent: "center", background: b.earned ? "rgba(200,75,49,0.08)" : "var(--sh-paper-2)" }}>
                <Icon name={b.earned ? b.icon : "lock"} size={22} color={b.earned ? "var(--sh-vermillion)" : "var(--sh-fog)"} />
              </div>
              <div className="sh-label" style={{ fontSize: 11.5, fontWeight: 600, color: b.earned ? "var(--sh-ink)" : "var(--sh-fog)" }}>{b.label}</div>
            </div>
          ))}
        </div>
        <button onClick={onStats} style={{ width: "100%", border: "none", cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center", gap: 8, padding: 16, borderRadius: 14, background: "transparent", boxShadow: "inset 0 0 0 1.5px var(--sh-line-2)", marginBottom: 24 }}>
          <Icon name="pulse" size={18} color="var(--sh-fog)" />
          <span style={{ fontFamily: "var(--sh-sans)", fontWeight: 600, fontSize: 14.5, color: "var(--sh-fog)" }}>View full stats</span>
        </button>
      </div>
      <TabBar active="profile" onNav={onNav} />
    </div>
  );
}

// ============================================================
// 17 · SETTINGS
// ============================================================
function SettingsGroup({ label, children }) {
  return (
    <div style={{ marginBottom: 22 }}>
      <div className="sh-kicker" style={{ marginBottom: 10, paddingLeft: 4 }}>{label}</div>
      <div className="sh-card" style={{ padding: "4px 18px" }}>{children}</div>
    </div>
  );
}
function SettingsScreen({ platform, onNav, onPro, onReferral, onSupport, onPrivacy, themeMode = "system", onThemeChange }) {
  const [proof, setProof] = shUseState(true);
  const [strict, setStrict] = shUseState(false);
  const [haptics, setHaptics] = shUseState(true);
  const [streakAlert, setStreakAlert] = shUseState(true);
  return (
    <div className="sh-screen">
      <div className="sh-body sh-pad" style={{ paddingTop: shTopInset(platform) }}>
        <h1 className="sh-title" style={{ fontSize: 28, marginTop: 16, marginBottom: 24 }}>Settings</h1>

        <SettingsGroup label="Account">
          <Row icon="user" title="Arjun Mehta" sub="+91 98765 43210" chevron />
          <Row icon="shield" title="Shoshin Pro" sub="Renews 12 Jul" chevron onClick={onPro} />
          <Row icon="gift" title="Invite the circle" sub="Earn a month of Pro" chevron onClick={onReferral} />
        </SettingsGroup>

        <SettingsGroup label="Practice">
          <Row icon="bolt" title="Default challenge" value="Standard" chevron />
          <Row icon="camera" title="Require photo proof" toggle={proof} onToggle={setProof} />
          <Row icon="lock" title="Strict mode" sub="No skips, no excuses" toggle={strict} onToggle={setStrict} />
        </SettingsGroup>

        <SettingsGroup label="Notifications">
          <Row icon="moon" title="Wind-down reminder" value="9:30 PM" chevron />
          <Row icon="flame" title="Streak alerts" toggle={streakAlert} onToggle={setStreakAlert} />
          <Row icon="clock" title="Quiet hours" value="10 PM – 5 AM" chevron />
        </SettingsGroup>

        <SettingsGroup label="App">
          <div style={{ display: "flex", alignItems: "center", gap: 14, padding: "15px 0", borderBottom: "1px solid var(--sh-line)" }}>
            <div className="sh-row-chip"><Icon name="sun" size={18} /></div>
            <div className="sh-row-main"><div className="sh-row-title">Appearance</div></div>
            <div style={{ display: "inline-flex", background: "var(--sh-paper-2)", borderRadius: 10, padding: 3, gap: 2 }}>
              {[["light", "Light"], ["dark", "Dark"], ["system", "Auto"]].map(([id, label]) => (
                <button key={id} onClick={() => onThemeChange && onThemeChange(id)} style={{
                  border: "none", cursor: "pointer", padding: "6px 12px", borderRadius: 7,
                  fontFamily: "var(--sh-sans)", fontWeight: 600, fontSize: 12.5,
                  background: themeMode === id ? "var(--sh-ink)" : "transparent",
                  color: themeMode === id ? "var(--sh-paper)" : "var(--sh-fog)",
                }}>{label}</button>
              ))}
            </div>
          </div>
          <Row icon="pulse" title="Haptics" toggle={haptics} onToggle={setHaptics} />
          <Row icon="info" title="About" value="v1.0" chevron />
        </SettingsGroup>

        <SettingsGroup label="Help">
          <Row icon="help" title="Help & support" chevron onClick={onSupport} />
          <Row icon="lock" title="Privacy & data" chevron onClick={onPrivacy} />
        </SettingsGroup>

        <div className="sh-card" style={{ padding: "4px 18px", marginBottom: 16 }}>
          <Row icon="logout" title="Sign out" danger />
        </div>
        <p className="sh-label" style={{ textAlign: "center", color: "var(--sh-fog-2)", marginBottom: 24 }}>Shoshin · Beginner's mind, every morning</p>
      </div>
    </div>
  );
}

// ============================================================
// 18 · ROUTINE EDITOR
// ============================================================
function RoutineEditorScreen({ platform, template = "walk", onSave }) {
  const t = SH_TEMPLATES[template];
  const verify = ["Tap", "Photo", "Photo", "Photo + GPS", "Photo + GPS"];
  return (
    <div className="sh-screen">
      <div className="sh-body sh-pad" style={{ paddingTop: shTopInset(platform) }}>
        <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginTop: 16, marginBottom: 22 }}>
          <h1 className="sh-title" style={{ fontSize: 28 }}>Edit path</h1>
          <Pill variant="accent">{t.tag}</Pill>
        </div>

        {/* name field */}
        <div style={{ marginBottom: 22 }}>
          <div className="sh-kicker" style={{ marginBottom: 10 }}>Path name</div>
          <div style={{ display: "flex", alignItems: "center", gap: 10, height: 56, padding: "0 16px", borderRadius: 14, background: "var(--sh-surface)", boxShadow: "inset 0 0 0 1.5px var(--sh-line-2)" }}>
            <span style={{ flex: 1, fontFamily: "var(--sh-sans)", fontWeight: 600, fontSize: 16, color: "var(--sh-ink)" }}>{t.name}</span>
            <Icon name="edit" size={19} color="var(--sh-fog)" />
          </div>
        </div>

        <div className="sh-kicker" style={{ marginBottom: 14, paddingLeft: 4 }}>Checkpoints · drag to reorder</div>
        {t.steps.map((s, i) => (
          <div key={i} className="sh-card" style={{ padding: "14px 16px", marginBottom: 10, display: "flex", alignItems: "center", gap: 12 }}>
            <div style={{ display: "flex", flexDirection: "column", gap: 3, cursor: "grab" }}>
              {[0, 1, 2].map((r) => (
                <div key={r} style={{ display: "flex", gap: 3 }}>
                  <span style={{ width: 3, height: 3, borderRadius: "50%", background: "var(--sh-fog-2)" }}></span>
                  <span style={{ width: 3, height: 3, borderRadius: "50%", background: "var(--sh-fog-2)" }}></span>
                </div>
              ))}
            </div>
            <div style={{ width: 38, height: 38, borderRadius: 11, background: "var(--sh-paper-2)", display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0 }}>
              <Icon name={s[0]} size={19} color="var(--sh-ink)" />
            </div>
            <div style={{ flex: 1 }}>
              <div className="sh-row-title">{s[1]}</div>
              <div className="sh-row-sub">{verify[i]}</div>
            </div>
            <Icon name="chevR" size={18} color="var(--sh-fog-2)" />
          </div>
        ))}

        {/* add */}
        <button style={{ width: "100%", border: "none", cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center", gap: 8, padding: 16, borderRadius: 14, background: "transparent", boxShadow: "inset 0 0 0 1.5px var(--sh-line-2)", marginBottom: 24 }}>
          <Icon name="plus" size={19} color="var(--sh-fog)" />
          <span style={{ fontFamily: "var(--sh-sans)", fontWeight: 600, fontSize: 14.5, color: "var(--sh-fog)" }}>Add checkpoint</span>
        </button>

        <Button variant="accent" onClick={onSave} style={{ marginBottom: 16 }}>Save path</Button>
      </div>
    </div>
  );
}

Object.assign(window, {
  SH_BADGES, ProfileScreen, SettingsScreen, RoutineEditorScreen,
});
