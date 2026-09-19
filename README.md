# MobSMP

Creator-basiertes Minecraft-SMP-Plugin mit zufälligen Mob-Klassen, Progression über Kills, Bounty-/Most-Wanted-System, Teams und Events.

## Konzept

Jeder Spieler startet mit einer zufälligen Mob-Klasse.  
Über Kills werden aktive Fähigkeiten freigeschaltet:

- ab **3 Kills**: Ability 1
- ab **5 Kills**: Ability 2

Bei Tod verlierst du Fortschritt (standardmäßig 2 Kills).  
Starke Spieler werden durch Bounties und das Most-Wanted-System aktiv gejagt.

## Features (im Plugin)

- Zufällige Mob-Klasse beim ersten Join
- Passive Effekte + individuelle Schwächen pro Klasse
- Aktive Abilities mit Cooldowns
- Kill-/Death-Progression inklusive HP-Reduktion bei zu vielen 0-Kill-Toden
- Reroll-System über Mob Soul + Reroll Book
- Bounty-System mit bis zu 4 Auftraggebern
- Most-Wanted-Koordinaten-Leak in Intervallen (nur Online-Zeit zählt)
- Teams (max. 3 Spieler standardmäßig)
- Stats-Tracking (Kills, Deaths, Blöcke, Mob-Kills, Player-Kills)
- Admin-gesteuerte Events
- Serverregeln technisch abgesichert (z. B. End Crystal/Anchor blockiert)

## Commands

### Spieler-Commands

#### `/ability <1|2>`
Nutzt deine aktive Klassenfähigkeit:

- `1` = 3-Kill-Ability
- `2` = 5-Kill-Ability

Hinweise:
- Ability 1 funktioniert erst ab 3 Kills
- Ability 2 funktioniert erst ab 5 Kills
- Cooldowns sind klassenabhängig
- Mit Permission `mobsmp.ability.nocooldown` wird kein Ability-Cooldown angewendet

#### `/bounty set <player>`
Setzt ein Bounty auf einen Spieler.

- kostet standardmäßig 5 XP-Level (`bounty-xp-cost`)
- max. 4 Contributor pro Bounty
- du kannst dich nicht selbst targeten

#### `/bounty list`
Zeigt alle aktiven Bounties.

#### `/bounty info <player>`
Zeigt, ob und mit wie vielen Contributoren ein Bounty auf einem Spieler liegt.

#### `/team create <name>`
Erstellt ein Team.

- Name: 2–9 Zeichen, nur `A-Z a-z 0-9 _`
- max. Teamgröße über Config (`max-team-size`, default 3)

#### `/team invite <player>`
Lädt einen Spieler ein (nur Team-Leader).

#### `/team accept`
Nimmt die letzte offene Team-Einladung an.

#### `/team leave`
Verlässt das Team.

#### `/team disband`
Löst das Team auf (nur Leader).

#### `/team info [name]`
Zeigt Team-Infos (eigenes Team oder per Name).

#### `/stats [player]`
Zeigt Statistiken:
- Total Kills
- Player Kills
- Mob Kills
- Deaths
- Blocks Placed
- Blocks Broken

### Admin-Commands

> Permission: `mobsmp.admin`

#### `/mobsmp reload`
Lädt die Config neu.

#### `/mobsmp setclass <player> <class>`
Setzt die Mob-Klasse eines Spielers.

#### `/mobsmp setkills <player> <amount>`
Setzt die aktuellen Progression-Kills eines Spielers.

#### `/mobsmp reset <player>`
Setzt Spielerstatus zurück (Kills, Death-Zähler, HP-Reduktion).

#### `/mobsmp info <player>`
Zeigt interne MobSMP-Daten eines Spielers.

#### `/mobsmp event start <DOUBLE_KILLS|BOUNTY_RUSH|SPECIAL_DROPS>`
Startet ein Event.

#### `/mobsmp event stop`
Stoppt das aktive Event.

## Wie die Systeme funktionieren

### Mob-Klassen & Abilities

Klassen im Plugin:

- ZOMBIE
- SKELETON
- CREEPER
- SPIDER
- ENDERMAN
- BLAZE
- WITCH
- IRON_GOLEM
- PHANTOM
- WITHER_SKELETON

Jede Klasse hat:
- 1 Passive
- 1 Ability bei 3 Kills
- 1 Ability bei 5 Kills
- 1 Schwäche

### Kill- und Death-System

- Kills erhöhen Progression (bis 5 relevant für Ability-Unlocks)
- Tod reduziert Kills (`kill-loss-on-death`, default 2)
- Bei 3 aufeinanderfolgenden Toden mit 0 Kills:
  - max HP werden reduziert (default 7 Herzen)
  - zur Wiederherstellung auf normal: 2 neue Kills

### Reroll-System

- Bei Player-Kill bekommst du ein **Mob Soul**
- Rezept für **Reroll Book**:
  - Form: `S S /  B  / S S`
  - `S` = Mob Soul
  - `B` = Buch
- Rechtsklick mit Reroll Book öffnet Klassen-GUI
- Gewählte Klasse wird direkt gesetzt, Buch wird verbraucht

### Bounty-System

- Spieler setzen Bounties per `/bounty set`
- Beim Kill eines Bounty-Targets:
  - Killer bekommt **+2 Kills**
  - Contributor erhalten Hotbar-/Rüstungsitems des Targets (round-robin, online)

### Most Wanted

- Spieler mit den meisten aktuellen Kills wird Most Wanted
- Koordinaten-Leak alle `most-wanted-interval-minutes` (default 60)
- Leak-Timer zählt nur, wenn Most Wanted online ist

### Teams

- Friendly-Checks sind in Abilities/PvP integriert
- Team-Infos werden zusätzlich über das Scoreboard-Team gepflegt

### Events

- `DOUBLE_KILLS`: Kills zählen doppelt
- `SPECIAL_DROPS`: zusätzliche/doppelte Block- und Mob-Drops
- `BOUNTY_RUSH`: als eigener Event-Typ vorhanden (von Admins startbar)

## Regeln / technische Einschränkungen (Plugin-seitig)

- End Crystals deaktiviert (Placement + Crafting + Nutzung)
- Respawn Anchors deaktiviert (Nutzung + Crafting)
- Cobweb-Placement nur für Spider-Klasse
- Enderperlen-Limit pro Spieler (default 8)
- Mace-Limit pro Spieler (default 1)
- Golden-Apple-Rezept optional angepasst (4 Gold + 1 Apfel), per Config aktivierbar

## Konfiguration

Datei: `src/main/resources/config.yml` (wird beim ersten Start als Plugin-Config erzeugt)

- `kill-loss-on-death`
- `max-kills-for-progression`
- `max-team-size`
- `bounty-xp-cost`
- `most-wanted-interval-minutes`
- `deaths-before-max-health-reduction`
- `reduced-max-health-hearts`
- `enderperls-per-player-limit` (Schreibweise wie im Plugin-Code)
- `mace-limit-per-player`
- `golden-apple-recipe`

## Build

Voraussetzungen:
- Java 21
- Maven

Build:

```bash
mvn clean package
```
