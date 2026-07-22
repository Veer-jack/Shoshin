// ============================================================
// Shoshin — Morning flow screens
//   Alarm Setup · Camera Verification · Checkpoint Completion
// Requires: icons.jsx, components.jsx, screens-hero.jsx (SH_TEMPLATES)
// ============================================================

const SH_DAYS = ["M", "T", "W", "T", "F", "S", "S"];
const SH_DIFFICULTY = [
  { id: "gentle", label: "Gentle", desc: "One easy problem · snooze allowed once" },
  { id: "standard", label: "Standard", desc: "Two problems · no snooze" },
  { id: "shoshin", label: "Shoshin", desc: "Three problems · photo proof · no snooze" },
];
const SH_CP_NOTE = [
  "Wake the mind before the body. Two breaths, then begin.",
  "Cold water resets the system. Thirty seconds is enough.",
  "Lay it out the night before — less to decide now.",
  "The threshold is the hardest step. Cross it.",
  "You're moving. This is the practice. This is the win.",
];

// ============================================================
// 09 · ALARM SETUP
// ============================================================
function AlarmSetupScreen({ platform, template = "walk", onSave, onSound }) {
  const [days, setDays] = shUseState([0, 1, 2, 3, 4]);
  const [diff, setDiff] = shUseState("standard");
  const [windDown, setWindDown] = shUseState(true);
  const t = SH_TEMPLATES[template];
  const toggleDay = (i) => setDays((d) => d.includes(i) ? d.filter((x) => x !== i) : [...d, i]);
  const cur = SH_DIFFICULTY.find((d) => d.id === diff);
  return (
    <div className="sh-screen">
      <div className="sh-body sh-pad" style={{ paddingTop: shTopInset(platform) }}>
        <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginTop: 16, marginBottom: 24 }}>
          <h1 className="sh-title" style={{ fontSize: 28 }}>Set your wake</h1>
          <Pill variant="ink">Tomorrow</Pill>
        </div>

        {/* time card */}
        <div className="sh-card" style={{ padding: "28px 24px", textAlign: "center", marginBottom: 14 }}>
          <div style={{ display: "flex", alignItems: "baseline", justifyContent: "center", gap: 4 }}>
            <span className="sh-num" style={{ fontSize: 64, color: "var(--sh-ink)" }}>05</span>
            <span className="sh-num" style={{ fontSize: 64, color: "var(--sh-line-2)" }}>:</span>
            <span className="sh-num" style={{ fontSize: 64, color: "var(--sh-ink)" }}>30</span>
            <span className="sh-num" style={{ fontSize: 20, color: "var(--sh-fog)", marginLeft: 6 }}>AM</span>
          </div>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "center", gap: 6, marginTop: 8 }}>
            <Icon name="sun" size={15} color="var(--sh-fog)" />
            <span className="sh-label">Sunrise 6:14 AM · 44 min of dawn</span>
          </div>
        </div>

        {/* repeat days */}
        <div className="sh-card" style={{ padding: 20, marginBottom: 14 }}>
          <div className="sh-kicker" style={{ marginBottom: 14 }}>Repeat</div>
          <div style={{ display: "flex", gap: 8 }}>
            {SH_DAYS.map((d, i) => {
              const on = days.includes(i);
              return (
                <button key={i} onClick={() => toggleDay(i)} style={{
                  flex: 1, aspectRatio: "1", maxWidth: 44, border: "none", cursor: "pointer", borderRadius: "50%",
                  fontFamily: "var(--sh-sans)", fontWeight: 600, fontSize: 14,
                  background: on ? "var(--sh-ink)" : "transparent", color: on ? "var(--sh-paper)" : "var(--sh-fog)",
                  boxShadow: on ? "none" : "inset 0 0 0 1.5px var(--sh-line-2)", transition: "all .15s ease",
                }}>{d}</button>
              );
            })}
          </div>
        </div>

        {/* attached routine */}
        <div className="sh-card" style={{ padding: "6px 20px", marginBottom: 14 }}>
          <Row icon="walk" title="Routine" sub={`${t.name} · ${t.steps.length} checkpoints`} chevron />
        </div>

        {/* wake challenge */}
        <div className="sh-card" style={{ padding: 20, marginBottom: 14 }}>
          <div className="sh-kicker" style={{ marginBottom: 14 }}>Wake challenge</div>
          <Segmented options={SH_DIFFICULTY.map((d) => ({ value: d.id, label: d.label }))} value={diff} onChange={setDiff} />
          <p className="sh-body-text" style={{ fontSize: 13.5, marginTop: 14 }}>{cur.desc}</p>
        </div>

        {/* sound + wind-down */}
        <div className="sh-card" style={{ padding: "6px 20px", marginBottom: 24 }}>
          <Row icon="bell" title="Sound" value="Temple bell" chevron onClick={onSound} />
          <Row icon="moon" title="Wind-down reminder" sub="9:30 PM nightly" toggle={windDown} onToggle={setWindDown} />
        </div>

        <Button variant="accent" onClick={onSave} style={{ marginBottom: 16 }}>Arm for tomorrow</Button>
      </div>
    </div>
  );
}

// ============================================================
// 11 · CAMERA VERIFICATION (dark)
// ============================================================
function CameraVerificationScreen({ platform, label = "Out the door", onCapture, onSkip }) {
  return (
    <div className="sh-screen dark" style={{ background: "#070707", paddingTop: shTopInset(platform) }}>
      <div className="sh-body" style={{ display: "flex", flexDirection: "column", padding: "0 20px 24px" }}>
        <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginTop: 14, marginBottom: 18 }}>
          <span className="sh-pill" style={{ background: "rgba(255,255,255,0.08)", color: "var(--sh-night-text)" }}>
            <Icon name="camera" size={13} color="var(--sh-vermillion)" stroke={1.8} /> VERIFY CHECKPOINT
          </span>
          <button onClick={onSkip} style={{ background: "none", border: "none", cursor: "pointer", fontFamily: "var(--sh-sans)", fontWeight: 600, fontSize: 13, color: "var(--sh-night-muted)" }}>Skip · −1 proof</button>
        </div>

        {/* viewfinder */}
        <div style={{ flex: 1, borderRadius: 22, background: "rgba(255,255,255,0.03)", position: "relative", overflow: "hidden", display: "flex", alignItems: "center", justifyContent: "center", marginBottom: 22 }}>
          {/* corner brackets in vermillion */}
          {[["top", "left"], ["top", "right"], ["bottom", "left"], ["bottom", "right"]].map(([v, h], i) => (
            <div key={i} style={{
              position: "absolute", [v]: 20, [h]: 20, width: 34, height: 34,
              [`border${v[0].toUpperCase() + v.slice(1)}`]: "2.5px solid var(--sh-vermillion)",
              [`border${h[0].toUpperCase() + h.slice(1)}`]: "2.5px solid var(--sh-vermillion)",
              [`border${v === "top" ? "TopLeftRadius" : "BottomLeftRadius"}`]: h === "left" ? 10 : 0,
              borderRadius: v === "top" ? (h === "left" ? "10px 0 0 0" : "0 10px 0 0") : (h === "left" ? "0 0 0 10px" : "0 0 10px 0"),
            }}></div>
          ))}
          <div style={{ textAlign: "center" }}>
            <Icon name="camera" size={40} color="rgba(255,255,255,0.25)" />
            <div className="sh-kicker" style={{ color: "rgba(255,255,255,0.3)", marginTop: 12 }}>Live camera · point at target</div>
          </div>
          {/* overlay caption */}
          <div style={{ position: "absolute", left: 20, right: 20, bottom: 20, padding: "14px 16px", borderRadius: 14, background: "rgba(0,0,0,0.5)", backdropFilter: "blur(8px)" }}>
            <div className="sh-kicker accent" style={{ marginBottom: 4 }}>Checkpoint 4</div>
            <div style={{ fontFamily: "var(--sh-sans)", fontWeight: 600, fontSize: 16, color: "#fff" }}>{label}</div>
          </div>
        </div>

        {/* shutter row */}
        <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
          <button style={{ width: 50, height: 50, borderRadius: "50%", border: "none", cursor: "pointer", background: "rgba(255,255,255,0.08)", display: "flex", alignItems: "center", justifyContent: "center" }}>
            <Icon name="moon" size={20} color="var(--sh-night-text)" />
          </button>
          <button onClick={onCapture} style={{ width: 76, height: 76, borderRadius: "50%", border: "4px solid rgba(255,255,255,0.85)", cursor: "pointer", background: "transparent", display: "flex", alignItems: "center", justifyContent: "center", padding: 4 }}>
            <div style={{ width: "100%", height: "100%", borderRadius: "50%", background: "var(--sh-vermillion)" }}></div>
          </button>
          <button style={{ width: 50, height: 50, borderRadius: "50%", border: "none", cursor: "pointer", background: "rgba(255,255,255,0.08)", display: "flex", alignItems: "center", justifyContent: "center" }}>
            <Icon name="user" size={20} color="var(--sh-night-text)" />
          </button>
        </div>
      </div>
    </div>
  );
}

// ============================================================
// 12 · CHECKPOINT COMPLETION
// ============================================================
function CheckpointCompletionScreen({ platform, template = "walk", current = 1, onComplete }) {
  const t = SH_TEMPLATES[template];
  const steps = t.steps;
  const total = steps.length;
  const pct = Math.round((current / total) * 100);
  const isLast = current === total - 1;
  const cur = steps[current];
  const times = ["05:33", "05:36", "05:41", "05:46", "05:52"];
  return (
    <div className="sh-screen" style={{ background: "var(--sh-paper-2)" }}>
      <div className="sh-body sh-pad" style={{ paddingTop: shTopInset(platform) }}>
        {/* header */}
        <div style={{ display: "flex", alignItems: "flex-start", justifyContent: "space-between", marginTop: 16, marginBottom: 20 }}>
          <div>
            <div className="sh-kicker accent" style={{ marginBottom: 6 }}>Crossing the bridge</div>
            <h1 className="sh-h2" style={{ fontSize: 18 }}>{t.name}</h1>
          </div>
          <div style={{ textAlign: "right" }}>
            <div className="sh-num" style={{ fontSize: 22 }}>05:46</div>
            <div className="sh-label" style={{ fontSize: 11 }}>+16 min elapsed</div>
          </div>
        </div>

        {/* progress */}
        <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 8 }}>
          <span className="sh-label" style={{ fontWeight: 600 }}>{current} of {total} kept</span>
          <span className="sh-num" style={{ fontSize: 14, color: "var(--sh-matcha)" }}>{pct}%</span>
        </div>
        <div style={{ height: 8, borderRadius: 4, background: "var(--sh-sand)", overflow: "hidden", marginBottom: 22 }}>
          <div style={{ height: "100%", width: `${pct}%`, background: "var(--sh-matcha)", borderRadius: 4, transition: "width .5s ease" }}></div>
        </div>

        {/* current checkpoint hero */}
        <div className="sh-card" style={{ padding: 26, marginBottom: 20, textAlign: "center", boxShadow: "inset 0 0 0 2px var(--sh-ink)" }}>
          <div style={{ width: 64, height: 64, borderRadius: 18, background: "var(--sh-ink)", display: "flex", alignItems: "center", justifyContent: "center", margin: "0 auto 16px" }}>
            <Icon name={cur[0]} size={30} color="var(--sh-paper)" />
          </div>
          <div className="sh-kicker" style={{ marginBottom: 8 }}>Current checkpoint</div>
          <h2 className="sh-title" style={{ fontSize: 26, marginBottom: 12 }}>{cur[1]}</h2>
          <p className="sh-body-text" style={{ fontSize: 14, maxWidth: 280, margin: "0 auto 22px" }}>{SH_CP_NOTE[current]}</p>
          <Button variant={isLast ? "accent" : "primary"} icon={isLast ? "play" : "check"} onClick={onComplete}>
            {isLast ? "Begin the habit" : "Mark complete"}
          </Button>
        </div>

        {/* full list */}
        <div className="sh-card" style={{ padding: "8px 18px", marginBottom: 24 }}>
          {steps.map((s, i) => (
            <Checkpoint key={i} index={i + 1} icon={s[0]} label={s[1]}
              state={i < current ? "done" : i === current ? "active" : "pending"}
              time={i < current ? times[i] : undefined} />
          ))}
        </div>
      </div>
    </div>
  );
}

Object.assign(window, {
  SH_DAYS, SH_DIFFICULTY, SH_CP_NOTE,
  AlarmSetupScreen, CameraVerificationScreen, CheckpointCompletionScreen,
});
