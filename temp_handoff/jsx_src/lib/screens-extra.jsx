// ============================================================
// Shoshin — Extra screens (Phase 2b)
//   Groups · Morning Complete · Shoshin Pro · History · Sound
// Requires: icons.jsx, components.jsx, screens-hero.jsx (SH_TEMPLATES)
// ============================================================

const SH_POD = [
  { initial: "A", name: "Arjun (you)", status: "practicing", streak: 14, you: true },
  { initial: "M", name: "Mei", status: "practicing", streak: 31 },
  { initial: "R", name: "Rahul", status: "practicing", streak: 9 },
  { initial: "S", name: "Sofia", status: "resting", streak: 22 },
  { initial: "K", name: "Kenji", status: "sleeping", streak: 5 },
];
const SH_SOUNDS = [
  { id: "bell", name: "Temple bell", note: "A single resonant strike" },
  { id: "bowl", name: "Singing bowl", note: "Slow rising hum" },
  { id: "bamboo", name: "Bamboo (shishi-odoshi)", note: "Gentle wooden knock" },
  { id: "birds", name: "Morning birds", note: "Dawn chorus" },
  { id: "koto", name: "Koto strings", note: "Soft plucked melody" },
  { id: "rain", name: "Light rainfall", note: "Steady and calm" },
  { id: "gong", name: "Distant gong", note: "Deep, for heavy sleepers" },
];

// ============================================================
// 25 · MORNING COMPLETE
// ============================================================
function MorningCompleteScreen({ platform, template = "walk", onClose }) {
  const t = SH_TEMPLATES[template];
  return (
    <div className="sh-screen" style={{ background: "var(--sh-paper-2)" }}>
      <div className="sh-body sh-pad" style={{ paddingTop: shTopInset(platform), display: "flex", flexDirection: "column" }}>
        <div style={{ flex: 1, display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", textAlign: "center" }}>
          {/* completed enso with check */}
          <div style={{ position: "relative", width: 200, height: 200, display: "flex", alignItems: "center", justifyContent: "center" }}>
            <svg width="200" height="200" viewBox="0 0 200 200" style={{ position: "absolute" }}>
              <circle cx="100" cy="100" r="86" fill="none" stroke="var(--sh-matcha)" strokeWidth="7"
                strokeLinecap="round" strokeDasharray={2 * Math.PI * 86} strokeDashoffset="0"
                transform="rotate(-90 100 100)" />
            </svg>
            <div style={{ width: 72, height: 72, borderRadius: "50%", background: "var(--sh-matcha)", display: "flex", alignItems: "center", justifyContent: "center" }}>
              <Icon name="check" size={36} color="#fff" stroke={2.2} />
            </div>
          </div>

          <div className="sh-kicker matcha" style={{ marginTop: 28, width: "100%" }}>The bridge is crossed</div>
          <h1 className="sh-title" style={{ fontSize: 32, width: "100%", marginTop: 8 }}>You've begun.</h1>
          <p className="sh-body-text" style={{ fontSize: 15, maxWidth: 290, marginTop: 10 }}>
            Five checkpoints, twenty-two minutes. The hardest part of the day is already behind you.
          </p>

          {/* summary */}
          <div className="sh-card" style={{ padding: 18, marginTop: 28, width: "100%", display: "flex", justifyContent: "space-around" }}>
            <Stat value="05:30" label="Started" align="center" />
            <div style={{ width: 1, background: "var(--sh-line)" }}></div>
            <Stat value="22" unit="min" label="Bridge" align="center" />
            <div style={{ width: 1, background: "var(--sh-line)" }}></div>
            <Stat value="5/5" label="Kept" align="center" color="var(--sh-matcha)" />
          </div>

          {/* streak bump */}
          <div style={{ display: "flex", alignItems: "center", gap: 8, marginTop: 16 }}>
            <Icon name="flame" size={18} color="var(--sh-vermillion)" />
            <span className="sh-label" style={{ fontWeight: 600, color: "var(--sh-ink)" }}>15 mornings kept</span>
            <Pill variant="matcha">+1</Pill>
          </div>
        </div>

        <div style={{ paddingBottom: 28 }}>
          <Button variant="primary" onClick={onClose}>Carry it into the day</Button>
          <p className="sh-label" style={{ textAlign: "center", marginTop: 16, color: "var(--sh-fog-2)" }}>Return again tomorrow.</p>
        </div>
      </div>
    </div>
  );
}

// ============================================================
// 26 · GROUPS / ACCOUNTABILITY PODS
// ============================================================
function GroupsScreen({ platform, onNav, onLeaderboard, onInvite }) {
  const statusMeta = {
    practicing: { label: "Practicing", color: "var(--sh-matcha)", bg: "rgba(74,124,89,0.12)" },
    resting: { label: "Resting", color: "var(--sh-fog)", bg: "var(--sh-paper-2)" },
    sleeping: { label: "Asleep", color: "var(--sh-fog-2)", bg: "var(--sh-paper-2)" },
  };
  const up = SH_POD.filter((m) => m.status === "practicing").length;
  return (
    <div className="sh-screen">
      <div className="sh-body sh-pad" style={{ paddingTop: shTopInset(platform) }}>
        <div style={{ marginTop: 16, marginBottom: 22 }}>
          <div className="sh-kicker" style={{ marginBottom: 8 }}>Accountability</div>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
            <h1 className="sh-title" style={{ fontSize: 30, whiteSpace: "nowrap" }}>Your circle</h1>
            <Pill variant="ink">Dawn Circle</Pill>
          </div>
        </div>

        {/* wake board */}
        <div className="sh-card ink" style={{ padding: 22, marginBottom: 14, position: "relative", overflow: "hidden" }}>
          <div style={{ position: "absolute", right: -34, top: -34, opacity: 0.35 }}><Enso size={150} color="#C84B31" strokeW={6} /></div>
          <div style={{ position: "relative" }}>
            <div className="sh-kicker" style={{ color: "rgba(242,241,236,0.6)", marginBottom: 10 }}>This morning · 5:30 AM</div>
            <div style={{ display: "flex", alignItems: "baseline", gap: 8 }}>
              <span className="sh-num" style={{ fontSize: 40, color: "var(--sh-paper)" }}>{up}</span>
              <span style={{ fontFamily: "var(--sh-display)", fontSize: 22, color: "rgba(242,241,236,0.7)" }}>of 5 have begun</span>
            </div>
            <p style={{ margin: "10px 0 0", fontFamily: "var(--sh-sans)", fontSize: 13.5, color: "rgba(242,241,236,0.7)" }}>You rose with Mei and Rahul. Sit together at dawn.</p>
          </div>
        </div>

        {/* members */}
        <div className="sh-card" style={{ padding: "6px 18px", marginBottom: 14 }}>
          {SH_POD.map((m, i) => {
            const sm = statusMeta[m.status];
            return (
              <div key={i} className="sh-row">
                <div style={{ width: 40, height: 40, borderRadius: "50%", background: m.you ? "var(--sh-ink)" : "var(--sh-sand)", display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0 }}>
                  <span style={{ fontFamily: "var(--sh-display)", fontWeight: 600, fontSize: 17, color: m.you ? "var(--sh-paper)" : "var(--sh-ink)" }}>{m.initial}</span>
                </div>
                <div className="sh-row-main">
                  <div className="sh-row-title">{m.name}</div>
                  <div style={{ display: "flex", alignItems: "center", gap: 5, marginTop: 3 }}>
                    <span style={{ width: 6, height: 6, borderRadius: "50%", background: sm.color }}></span>
                    <span className="sh-label" style={{ fontSize: 12, color: sm.color, fontWeight: 600 }}>{sm.label}</span>
                  </div>
                </div>
                <div style={{ display: "flex", alignItems: "center", gap: 5 }}>
                  <Icon name="flame" size={15} color="var(--sh-vermillion)" />
                  <span className="sh-num" style={{ fontSize: 15 }}>{m.streak}</span>
                </div>
              </div>
            );
          })}
        </div>

        {/* invite */}
        <button onClick={onInvite} style={{ width: "100%", border: "none", cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center", gap: 8, padding: 16, borderRadius: 14, background: "transparent", boxShadow: "inset 0 0 0 1.5px var(--sh-line-2)", marginBottom: 12 }}>
          <Icon name="plus" size={19} color="var(--sh-fog)" />
          <span style={{ fontFamily: "var(--sh-sans)", fontWeight: 600, fontSize: 14.5, color: "var(--sh-fog)" }}>Invite someone to the circle</span>
        </button>
        <button onClick={onLeaderboard} style={{ width: "100%", border: "none", cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center", gap: 8, padding: 16, borderRadius: 14, background: "var(--sh-paper-2)", marginBottom: 24 }}>
          <Icon name="crown" size={18} color="var(--sh-vermillion)" />
          <span style={{ fontFamily: "var(--sh-sans)", fontWeight: 600, fontSize: 14.5, color: "var(--sh-ink)" }}>View leaderboard</span>
        </button>
      </div>
      <TabBar active="groups" onNav={onNav} />
    </div>
  );
}

// ============================================================
// 27 · SHOSHIN PRO (paywall)
// ============================================================
function PaywallScreen({ platform, onStart, onClose }) {
  const [plan, setPlan] = shUseState("year");
  const feats = [
    ["bolt", "Unlimited paths", "Build every routine you practice"],
    ["camera", "Photo & GPS proof", "Verify each checkpoint your way"],
    ["shield", "71-Day Discipline", "The advanced identity challenge"],
    ["users", "Accountability circles", "Rise together with your pod"],
    ["cal", "Full history", "Every morning, kept forever"],
  ];
  return (
    <div className="sh-screen dark">
      <div className="sh-body" style={{ paddingTop: shTopInset(platform), padding: `${shTopInset(platform)}px 24px 0`, display: "flex", flexDirection: "column" }}>
        <div style={{ display: "flex", justifyContent: "flex-end", marginTop: 8 }}>
          <button onClick={onClose} style={{ background: "rgba(255,255,255,0.08)", border: "none", cursor: "pointer", width: 34, height: 34, borderRadius: "50%", display: "flex", alignItems: "center", justifyContent: "center" }}>
            <Icon name="plus" size={20} color="var(--sh-night-muted)" style={{ transform: "rotate(45deg)" }} />
          </button>
        </div>

        <div style={{ textAlign: "center", marginTop: 4, marginBottom: 22 }}>
          <div style={{ display: "inline-flex" }}><LogoMark size={44} on="night" /></div>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "center", gap: 8, marginTop: 14 }}>
            <span style={{ fontFamily: "var(--sh-display)", fontWeight: 600, fontSize: 32, color: "var(--sh-night-text)" }}>Shoshin</span>
            <span className="sh-pill accent" style={{ fontSize: 12 }}>PRO</span>
          </div>
          <p className="sh-body-text" style={{ fontSize: 14.5, color: "var(--sh-night-muted)", marginTop: 8 }}>Go deeper into the practice.</p>
        </div>

        {/* features */}
        <div style={{ display: "flex", flexDirection: "column", gap: 16, marginBottom: 24 }}>
          {feats.map(([ic, t, d], i) => (
            <div key={i} style={{ display: "flex", alignItems: "center", gap: 14 }}>
              <div style={{ width: 40, height: 40, borderRadius: 11, background: "rgba(255,255,255,0.06)", display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0 }}>
                <Icon name={ic} size={20} color="var(--sh-vermillion)" />
              </div>
              <div style={{ flex: 1 }}>
                <div style={{ fontFamily: "var(--sh-sans)", fontWeight: 600, fontSize: 15.5, color: "var(--sh-night-text)" }}>{t}</div>
                <div className="sh-label" style={{ fontSize: 12.5, color: "var(--sh-night-muted)", marginTop: 1 }}>{d}</div>
              </div>
            </div>
          ))}
        </div>

        <div style={{ flex: 1 }}></div>

        {/* plans */}
        <div style={{ display: "flex", gap: 12, marginBottom: 16 }}>
          {[["month", "Monthly", "₹299", "/mo"], ["year", "Yearly", "₹1,999", "/yr"]].map(([id, label, price, per]) => {
            const on = plan === id;
            return (
              <button key={id} onClick={() => setPlan(id)} style={{
                flex: 1, cursor: "pointer", border: "none", padding: "16px 14px", borderRadius: 16, textAlign: "left", position: "relative",
                background: on ? "rgba(200,75,49,0.12)" : "rgba(255,255,255,0.04)",
                boxShadow: on ? "inset 0 0 0 2px var(--sh-vermillion)" : "inset 0 0 0 1px var(--sh-night-line)",
              }}>
                {id === "year" && <span style={{ position: "absolute", top: -9, right: 12 }} className="sh-pill accent">SAVE 44%</span>}
                <div className="sh-label" style={{ fontSize: 12, color: "var(--sh-night-muted)", fontWeight: 600 }}>{label}</div>
                <div style={{ display: "flex", alignItems: "baseline", gap: 2, marginTop: 6 }}>
                  <span className="sh-num" style={{ fontSize: 22, color: "var(--sh-night-text)" }}>{price}</span>
                  <span className="sh-label" style={{ fontSize: 12, color: "var(--sh-night-muted)" }}>{per}</span>
                </div>
              </button>
            );
          })}
        </div>

        <Button variant="accent" onClick={onStart}>Begin Pro · 7 days free</Button>
        <div style={{ display: "flex", justifyContent: "center", gap: 18, padding: "16px 0 24px" }}>
          <button onClick={onClose} style={{ background: "none", border: "none", cursor: "pointer", fontFamily: "var(--sh-sans)", fontSize: 13, color: "var(--sh-night-muted)", fontWeight: 600 }}>Restore</button>
          <button onClick={onClose} style={{ background: "none", border: "none", cursor: "pointer", fontFamily: "var(--sh-sans)", fontSize: 13, color: "var(--sh-night-muted)", fontWeight: 600 }}>Maybe later</button>
        </div>
      </div>
    </div>
  );
}

// ============================================================
// 28 · HISTORY / CALENDAR
// ============================================================
function HistoryScreen({ platform, onBack }) {
  // 30-day month: states by day index (1..30)
  const miss = [4, 11, 19];
  const today = 24;
  const firstDow = 6; // month starts on Saturday (0=Sun)
  const cells = [];
  for (let i = 0; i < firstDow; i++) cells.push(null);
  for (let d = 1; d <= 30; d++) cells.push(d);
  const dow = ["S", "M", "T", "W", "T", "F", "S"];
  return (
    <div className="sh-screen">
      <div className="sh-body sh-pad" style={{ paddingTop: shTopInset(platform) }}>
        <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginTop: 16, marginBottom: 22 }}>
          <button onClick={onBack} style={{ background: "none", border: "none", cursor: "pointer", padding: 0 }}><Icon name="arrowL" size={24} color="var(--sh-ink)" /></button>
          <h1 className="sh-h2" style={{ fontSize: 17 }}>June 2026</h1>
          <button style={{ background: "none", border: "none", cursor: "pointer", padding: 0 }}><Icon name="chevR" size={22} color="var(--sh-fog-2)" /></button>
        </div>

        {/* month summary */}
        <div style={{ display: "flex", gap: 12, marginBottom: 20 }}>
          <div className="sh-card" style={{ padding: 16, flex: 1 }}>
            <div className="sh-num" style={{ fontSize: 24, color: "var(--sh-matcha)" }}>21</div>
            <div className="sh-kicker" style={{ fontSize: 9.5, marginTop: 3 }}>Kept</div>
          </div>
          <div className="sh-card" style={{ padding: 16, flex: 1 }}>
            <div className="sh-num" style={{ fontSize: 24, color: "var(--sh-vermillion)" }}>3</div>
            <div className="sh-kicker" style={{ fontSize: 9.5, marginTop: 3 }}>Missed</div>
          </div>
          <div className="sh-card" style={{ padding: 16, flex: 1 }}>
            <div className="sh-num" style={{ fontSize: 24 }}>88<span style={{ fontSize: 12, color: "var(--sh-fog)" }}>%</span></div>
            <div className="sh-kicker" style={{ fontSize: 9.5, marginTop: 3 }}>Rate</div>
          </div>
        </div>

        {/* calendar */}
        <div className="sh-card" style={{ padding: 18, marginBottom: 18 }}>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(7, 1fr)", gap: 6, marginBottom: 10 }}>
            {dow.map((d, i) => <div key={i} className="sh-label" style={{ textAlign: "center", fontSize: 11, color: "var(--sh-fog-2)", fontWeight: 600 }}>{d}</div>)}
          </div>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(7, 1fr)", gap: 6 }}>
            {cells.map((d, i) => {
              if (d === null) return <div key={i}></div>;
              const isMiss = miss.includes(d);
              const isToday = d === today;
              const future = d > today;
              const kept = !isMiss && !future;
              return (
                <div key={i} style={{
                  aspectRatio: "1", borderRadius: 9, display: "flex", alignItems: "center", justifyContent: "center",
                  background: kept ? "var(--sh-matcha)" : "transparent",
                  boxShadow: isToday ? "inset 0 0 0 2px var(--sh-vermillion)" : isMiss ? "inset 0 0 0 1.5px var(--sh-line-2)" : "none",
                }}>
                  <span className="sh-num" style={{ fontSize: 12, color: kept ? "#fff" : isToday ? "var(--sh-vermillion)" : isMiss ? "var(--sh-fog-2)" : "var(--sh-fog-2)" }}>{d}</span>
                </div>
              );
            })}
          </div>
        </div>

        {/* selected day detail */}
        <div className="sh-kicker" style={{ marginBottom: 12, paddingLeft: 4 }}>Friday, 23 June</div>
        <div className="sh-card" style={{ padding: 20, marginBottom: 24 }}>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 16 }}>
            <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
              <Icon name="walk" size={20} color="var(--sh-ink)" />
              <span className="sh-h2">Morning Walk</span>
            </div>
            <Pill variant="matcha" icon="check">Kept</Pill>
          </div>
          <div style={{ display: "flex", justifyContent: "space-between" }}>
            <Stat value="05:31" label="Started" />
            <Stat value="20" unit="min" label="Bridge" />
            <Stat value="5/5" label="Checkpoints" color="var(--sh-matcha)" />
          </div>
        </div>
      </div>
    </div>
  );
}

// ============================================================
// 29 · SOUND PICKER
// ============================================================
function SoundPickerScreen({ platform, onBack }) {
  const [sel, setSel] = shUseState("bell");
  const [vol, setVol] = shUseState(70);
  return (
    <div className="sh-screen">
      <div className="sh-body sh-pad" style={{ paddingTop: shTopInset(platform) }}>
        <div style={{ display: "flex", alignItems: "center", gap: 14, marginTop: 16, marginBottom: 24 }}>
          <button onClick={onBack} style={{ background: "none", border: "none", cursor: "pointer", padding: 0 }}><Icon name="arrowL" size={24} color="var(--sh-ink)" /></button>
          <h1 className="sh-title" style={{ fontSize: 26, whiteSpace: "nowrap" }}>Wake sound</h1>
        </div>

        {/* volume */}
        <div className="sh-card" style={{ padding: 20, marginBottom: 20 }}>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 14 }}>
            <span className="sh-kicker">Volume</span>
            <span className="sh-num" style={{ fontSize: 14 }}>{vol}%</span>
          </div>
          <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
            <Icon name="moon" size={18} color="var(--sh-fog-2)" />
            <input type="range" min="0" max="100" value={vol} onChange={(e) => setVol(Number(e.target.value))}
              style={{ flex: 1, accentColor: "var(--sh-vermillion)", height: 4 }} />
            <Icon name="bell" size={18} color="var(--sh-fog)" />
          </div>
        </div>

        <div className="sh-kicker" style={{ marginBottom: 12, paddingLeft: 4 }}>Sounds</div>
        <div className="sh-card" style={{ padding: "4px 18px", marginBottom: 24 }}>
          {SH_SOUNDS.map((s) => {
            const on = sel === s.id;
            return (
              <div key={s.id} className="sh-row" onClick={() => setSel(s.id)} style={{ cursor: "pointer" }}>
                <div style={{ width: 38, height: 38, borderRadius: 11, background: on ? "var(--sh-ink)" : "var(--sh-paper-2)", display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0 }}>
                  <Icon name="play" size={16} color={on ? "var(--sh-paper)" : "var(--sh-ink)"} />
                </div>
                <div className="sh-row-main">
                  <div className="sh-row-title">{s.name}</div>
                  <div className="sh-row-sub">{s.note}</div>
                </div>
                {on && <Icon name="check" size={20} color="var(--sh-matcha)" stroke={2.2} />}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

Object.assign(window, {
  SH_POD, SH_SOUNDS,
  MorningCompleteScreen, GroupsScreen, PaywallScreen, HistoryScreen, SoundPickerScreen,
});
