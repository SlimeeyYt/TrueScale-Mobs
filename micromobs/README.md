# MicroMobs — Realistic Mob Sizes

A Fabric mod for **Minecraft 1.21.1** that rescales mobs to closer real-world
biological proportions.

---

## Scaling at a glance

| Category | Examples | Scale |
|---|---|---|
| Tiny insects | Silverfish, Endermite | 10 % |
| Small insects | Bee | 18 % |
| Small arthropods | Cave Spider, Spider | 22 – 25 % |
| Small aquatic | Pufferfish, Tadpole | 20 – 40 % |
| Amphibians | Frog | 35 % |
| Small animals | Rabbit, Parrot, Fox, Cat | 60 – 75 % |
| Medium animals | Pig, Sheep, Wolf, Cow | 90 – 95 % |
| Aquatic | Cod, Salmon, Turtle, Squid | 80 – 95 % |
| Slime / Magma Cube | Base size −30 % | 70 % |
| Large / Bosses | Iron Golem, Warden, Ender Dragon | 100 % (unchanged) |
| Monsters | Zombie, Skeleton, Creeper, Enderman… | 100 % (unchanged) |

---

## Features

- **GENERIC_SCALE attribute** — scales model, hitbox, eye-height and shadow in
  one shot using Minecraft's built-in attribute system (no render hacks).
- **Speed boost for tiny mobs** — Spider ×1.2, Bee ×1.15, Silverfish & Endermite ×1.3.
- **Higher pitch sounds** — Bee, Silverfish, Endermite play at elevated pitch.
- **Persistent modifiers** — stored in NBT so the modifier is only written once
  per entity, even across world reloads.
- **JSON config** — every scale is tuneable via `config/micromobs.json`.

---

## Requirements

| Dependency | Version |
|---|---|
| Java (JDK) | 21+ (or any JDK — toolchain auto-provisions 21) |
| Minecraft | 1.21.1 |
| Fabric Loader | 0.18.4 |
| Fabric API | 0.116.9+1.21.1 |
| Gradle wrapper | 9.2.1 (auto-downloaded) |
| Fabric Loom | 1.15.5 |

---

## Building

### Quick local testing environment (recommended)

From the project root, run:

```powershell
cd "D:\\Slimeey Studios\\actual size\\micromobs"
Set-ExecutionPolicy -Scope Process Bypass -Force
.\\test-client.ps1 -PrepOnly
```

This pre-downloads assets, prepares Loom launch files, and generates IDEA sync data.
Then launch the dev client with:

```powershell
.\\gradlew.bat runClient
```

### 1 - Prerequisites

Install **Java 21 JDK** and make sure `JAVA_HOME` points to it:

```powershell
$env:JAVA_HOME = "C:\path\to\jdk-21"
java -version   # should print 21.x.x
```

### 2 — Download the Gradle wrapper JAR

The wrapper JAR is not included in source control.  Run **one** of the
following depending on what you have installed:

**Option A - Gradle installed globally:**
```powershell
cd "D:\\Slimeey Studios\\actual size\\micromobs"
gradle wrapper --gradle-version 9.2.1
```

**Option B — Use the provided setup script:**
```powershell
cd "D:\\Slimeey Studios\\actual size\\micromobs"
.\setup.ps1
```

**Option C - Download manually:**
Download `gradle-wrapper.jar` from:
`https://raw.githubusercontent.com/gradle/gradle/v9.2.1/gradle/wrapper/gradle-wrapper.jar`
and place it at `gradle/wrapper/gradle-wrapper.jar`.

### 3 — Build

```powershell
.\gradlew.bat build
```

> The **first build** downloads Minecraft assets and yarn mappings (~500 MB).
> Subsequent builds are fast.

The compiled JAR will be at:
```
build\libs\micromobs-1.0.0.jar
```

---

## Installation

1. Install [Fabric Loader 0.18.4+](https://fabricmc.net/use/) for Minecraft 1.21.1.
2. Install [Fabric API](https://modrinth.com/mod/fabric-api) for 1.21.1.
3. Drop `micromobs-1.0.0.jar` into your `.minecraft/mods/` folder.
4. Launch Minecraft 1.21.1 with the Fabric profile.

On first launch the config file is written to:
```
.minecraft/config/micromobs.json
```

---

## Configuration

Edit `config/micromobs.json` while the game is closed:

```json
{
  "spiderScale": 0.25,
  "caveSpiderScale": 0.22,
  "beeScale": 0.18,
  "silverfishScale": 0.10,
  "endermiteScale": 0.10,
  "frogScale": 0.35,
  "tadpoleScale": 0.20,
  "chickenScale": 0.75,
  "rabbitScale": 0.60,
  "foxScale": 0.70,
  "catScale": 0.70,
  "ocelotScale": 0.70,
  "parrotScale": 0.65,
  "pigScale": 0.90,
  "sheepScale": 0.90,
  "cowScale": 0.95,
  "goatScale": 0.90,
  "wolfScale": 0.90,
  "pandaScale": 0.95,
  "codScale": 0.80,
  "salmonScale": 0.90,
  "pufferfishScale": 0.40,
  "tropicalFishScale": 0.70,
  "squidScale": 0.80,
  "glowSquidScale": 0.80,
  "axolotlScale": 0.50,
  "turtleScale": 0.80,
  "dolphinScale": 0.95,
  "slimeScale": 0.70,
  "magmaCubeScale": 0.70,
  "enableMovementAdjustments": true,
  "enableSoundAdjustments": true
}
```

Set `enableMovementAdjustments` to `false` to disable the speed boost for tiny
mobs.  Set `enableSoundAdjustments` to `false` to keep vanilla sound pitch.

---

## Technical notes

### Why GENERIC_SCALE?

`GENERIC_SCALE` (added in Minecraft 1.20.5) is the official attribute for
proportional entity scaling.  Setting it to 0.25 scales the entire entity —
hitbox, eye-height, shadow, renderer — in one attribute write.  No per-tick
code, no render injection required.

### Modifier lifetime

`addPersistentModifier()` saves the modifier in the entity's NBT tag.  The
`ENTITY_LOAD` listener checks whether the modifier is already present before
writing, so the scale is applied exactly **once** per entity — whether the mob
spawned naturally, from a spawner, via command, or by spawn egg.

### Slimes & Magma Cubes

These mobs scale their dimensions dynamically based on their "size" tag
(1, 2, 4).  Applying `GENERIC_SCALE = 0.70` uniformly reduces every size tier
by 30 %, so a large slime becomes 70 % of its vanilla large size, etc.

### Sound pitch

The mixin injects at the `RETURN` point of `LivingEntity.getSoundPitch()` and
multiplies the returned value.  All ambient, hurt, and death sounds for the
entity route through this single method, so one injection covers everything.
The injection uses `require = 0` — if the method signature ever changes in a
future Yarn build the mixin silently no-ops rather than crashing.

---

## Troubleshooting

| Symptom | Fix |
|---|---|
| `Attribute GENERIC_SCALE not found` | Update to Minecraft 1.21.1; GENERIC_SCALE requires ≥ 1.20.5 |
| `EntityAttributeModifier.Operation.ADD_VALUE` not found | You are on a pre-1.20.5 mapping; use `ADD` instead |
| Wrapper JAR missing | Run `gradle wrapper --gradle-version 9.2.1` or download it manually (see step 2) |
| Sound mixin fails silently | Normal behaviour — `require = 0` means an incompatible method signature just disables the pitch injection |
| Mob still vanilla size after installing | The mod requires Fabric API on the mods list; double-check it is present |

---

## Project structure

```
micromobs/
├── build.gradle
├── gradle.properties
├── settings.gradle
├── gradlew.bat
├── LICENSE
├── README.md
├── gradle/wrapper/
│   └── gradle-wrapper.properties
└── src/main/
    ├── java/com/slimeey/micromobs/
    │   ├── MicroMobs.java            ← mod entrypoint
    │   ├── MicroMobsConfig.java      ← JSON config loader / saver
    │   ├── MobScaleManager.java      ← scale table + event registration
    │   └── mixin/
    │       └── LivingEntitySoundMixin.java  ← pitch adjustment
    └── resources/
        ├── fabric.mod.json
        ├── micromobs.mixins.json
        └── pack.mcmeta
```
