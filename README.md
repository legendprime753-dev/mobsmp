# MobSMP

Ein Creator-basiertes Minecraft Plugin, bei dem Spieler als zufälliger Mob starten. Kills schalten Fähigkeiten frei. Inklusive Bounty-System, Most-Wanted Tracking und Reroll-Klassen-Mechanik.

## Features
- **14 einzigartige Mob-Klassen** mit folgenden passiven Werten und aktiven Fähigkeiten (ausgelöst per Rechtsklick [3 Kills] / Shift+Rechtsklick [5 Kills] mit dem Token):
  - **🧟 Zombie:** (Passiv: Stärke 1) | **3-Kills:** Nächster Hit infiziert (Wither + Hunger) | **5-Kills:** Resistenz 3 für 5s
  - **🏹 Skelett:** (Passiv: Speed 1) | **3-Kills:** Erhält einen Bogen (Stärke 3, Unendlichkeit) | **5-Kills:** Schießt Spektralpfeil (leuchtet Ziele durch Wände an)
  - **🕷️ Spinne:** (Passiv: 7 Herzen, Sprungkraft 2) | **3-Kills:** Wirft Spinnenweben-Schneeball | **5-Kills:** Gift-Flächeneffekt
  - **💥 Creeper:** (Passiv: Kein Fall-/TNT-Schaden) | **3-Kills:** Nächster Hit erzeugt einen Blitz | **5-Kills:** Spawnt 3 gezündete TNTs
  - **🔮 Enderman:** (Passiv: 7 Herzen, Speed 1) | **3-Kills:** Erhält 3 Enderperlen | **5-Kills:** Blindheit im Umkreis von 10 Blöcken
  - **🟢 Slime:** (Passiv: Sprungkraft 2, Kein Fallschaden) | **3-Kills:** Schleimball mit Rückstoß 2 | **5-Kills:** Sprungkraft 4 für 10s
  - **🛡️ Iron Golem:** (Passiv: 15 Herzen, Slowness 1, Resistenz 1) | **3-Kills:** Stock mit Rückstoß 3 | **5-Kills:** Regeneration 3 für 5s
  - **🦇 Phantom:** (Passiv: 7 Herzen, Elytra-Flug) | **3-Kills:** Levitation für Gegner in der Nähe | **5-Kills:** Blindheit für Gegner in der Nähe
  - **🐷 Piglin:** (Passiv: Goldschwert Schärfe 2) | **3-Kills:** Droppt zufälligen Loot (Gold, Gapples) | **5-Kills:** Stärke 2 für 10s
  - **💀 Warden:** (Passiv: Blindheit 1, Stärke 2, Resistenz 2) | **3-Kills:** Sonic Boom Hitbox nach vorn | **5-Kills:** Darkness-Effekt Flächenangriff
  - **🗡️ Wither Skeleton:** (Passiv: Feuerresistenz) | **3-Kills:** Steinschwert mit Wither-Effekt | **5-Kills:** Schießt 3 Wither-Skulls
  - **🔥 Blaze:** (Passiv: Feuerresistenz) | **3-Kills:** Schießt 3 Feuerbälle | **5-Kills:** Setzt Gegner im Umkreis von 5 Blöcken in Brand
  - **👨‍🌾 Villager:** (Passiv: Keine) | **3-Kills:** Wirft zufällige Emeralds / nützliche Items | **5-Kills:** Erhält ein Totem der Unsterblichkeit
  - **🐔 Huhn:** (Passiv: 5 Herzen, Sanfter Fall) | **3-Kills:** Wirft Ei, das Blindheit verursacht | **5-Kills:** Speed 3 für 10s
- PersistentDataContainer Tokens zur Nutzung der Fähigkeiten.
- Dynamisches Bounty System (Loot-Verteilung aus dem Inventar des Opfers).
- Most Wanted Timer & Coordinate Leaks (inkl. Glowing Effekt).
- Custom Reroll-System durch Fragmente.
- **NEU:** Anti-Kill-Boosting (Verhindert XP-Farming bei Freunden).
- **NEU:** Combat-Log-System (Verhindert Fliehen aus Kämpfen).
- **NEU:** Custom Scoreboard & Actionbar für Cooldowns.

## Items & Crafting (Custom Recipes)

### 🌟 Mob Token (Netherstern)
- **Erhalten:** Jeder Spieler erhält dieses Item automatisch beim ersten Joinen auf dem Server. Sollte man sterben, bekommt man es nach dem Respawn sofort zurück.
- **Funktion:** Rechtsklick führt die 3-Kill-Fähigkeit aus, Shift + Rechtsklick die 5-Kill-Fähigkeit. Es kann nicht gedroppt, gecraftet oder zerstört werden.

### 🔮 Kill-Fragment (Amethystscherbe)
- **Erhalten:** Wenn du einen Spieler tötest, besteht eine **20% Chance**, dass der getötete Spieler dieses Item fallen lässt (40% bei aktivem `SPECIAL_DROPS` Event).
- **Funktion:** Wird benötigt, um das Reroll-Buch herzustellen.

### 📚 Reroll Buch (Verzaubertes Buch)
- **Crafting-Rezept:** 
  - 4x Kill-Fragment (als Kreuz angeordnet)
  - 1x Buch (in die Mitte)
  *(Format: Oben-Mitte, Links, Mitte (Buch), Rechts, Unten-Mitte)*
- **Funktion:** Ein Rechtsklick auf das Reroll-Buch ändert deine Mob-Klasse zufällig zu einer anderen Klasse (nie zu der, die du gerade hast). Danach erhältst du sofort für 5 Sekunden Unverwundbarkeit.

### 🍎 Goldener Apfel (Günstiger)
- **Crafting-Rezept:**
  - 4x Goldbarren (als Kreuz angeordnet: Oben, Unten, Links, Rechts)
  - 1x Apfel (in die Mitte)
- **Hinweis:** Das normale Vanilla-Rezept für den Goldenen Apfel (8 Goldbarren) wurde komplett deaktiviert.

## Commands

### Spieler Commands
- `/mobsmp info` (bzw. `concept`) - Zeigt das detaillierte Konzept und die Regeln des Projekts grafisch an.
- `/team create <Name>` - Erstellt ein neues Team (max. 3 Mitglieder, Friendly Fire deaktiviert).
- `/team invite <Spieler>` - Lädt einen Spieler in dein Team ein.
- `/team accept` - Nimmt eine ausstehende Teameinladung an.
- `/team leave` - Verlässt das aktuelle Team.
- `/tc <Nachricht>` - Sendet eine private Nachricht an alle Mitglieder deines aktuellen Teams.
- `/bounty <Spieler>` - Öffnet ein GUI, in das du Items als Kopfgeld-Belohnung legen kannst.
- `/stats [Spieler]` - Zeigt dir deine eigenen oder die Statistiken eines anderen Spielers an (Kills, Tode, abgebaute Blöcke, etc.).

### Admin Commands (Benötigt `mobsmp.admin`)
- `/mobsmp setclass <Spieler> <Klasse>` - Ändert die Mob-Klasse eines Spielers manuell.
- `/mobsmp reset <Spieler>` - Setzt die Kills/Progression eines Spielers auf 0 zurück.
- `/mobsmp event start <Event>` - Startet ein Global-Event (Mögliche: `DOUBLE_KILLS`, `BOUNTY_RUSH`, `SPECIAL_DROPS`).
- `/mobsmp event stop [Event]` - Stoppt ein spezifisches Event oder alle aktiven Events, wenn keins angegeben wird.

## Konfiguration (config.yml)
In der `config.yml` lassen sich viele Systeme anpassen oder komplett deaktivieren (Toggles):

- **features.combat-log-system:** (true/false) Aktiviert das Combat-Logging System.
- **combat-log.duration-seconds:** Dauer des Combat-Logs.
- **combat-log.kill-on-logout:** (true/false) Ob der Spieler sterben soll, wenn er sich im Kampf ausloggt.
- **features.anti-kill-boosting:** (true/false) Aktiviert den Farming-Schutz.
- **anti-kill-boosting.cooldown-minutes-per-player:** (Zahl) Dauer, bis derselbe Spieler wieder einen Punkt gibt.
- **features.scoreboard / actionbar-cooldowns:** (true/false) UI-Elemente deaktivieren.
- **bounty.cost-enabled / cost-item / cost-amount:** Stellt die Bounty-Kosten ein.
