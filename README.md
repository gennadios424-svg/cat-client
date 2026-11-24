# Astral Cat Client

A small, purely cosmetic Fabric mod for Minecraft **1.21.1** (client-side only).

It hooks the title screen and the in-game pause menu and rains little
pixel-art cats down the screen. That's the whole feature set:

- No automation, macros, or input injection
- No server/network interaction of any kind
- No account, credential, or inventory access
- Toggle on/off anytime with `,` (comma) by default, rebindable in
  Options → Controls → Astral Cat Client
- Settings are a plain, hand-editable JSON file at
  `.minecraft/config/astralcatclient.json` (spawn rate, fall speed, size,
  max cat count, which screens it shows on)

## Project layout

```
astral-cat-client/
├── build.gradle
├── gradle.properties
├── settings.gradle
├── LICENSE
└── src/main/
    ├── java/com/astral/catclient/
    │   ├── AstralCatClient.java        (client entrypoint, screen hooks, keybind)
    │   ├── config/CatConfig.java       (JSON-backed settings)
    │   └── render/
    │       ├── FallingCat.java         (one cat: position, drawing)
    │       └── CatRainOverlay.java     (spawns/ticks/draws the list of cats)
    └── resources/
        ├── fabric.mod.json
        └── assets/astralcatclient/lang/en_us.json
```

No texture files are required — each cat is drawn as flat-colored
rectangles on an 8×7 pixel grid, so there's nothing that can go missing at
build time.

## Building on Windows

### 1. Install prerequisites

- **JDK 21** (e.g. [Eclipse Temurin 21](https://adoptium.net/)). Fabric 1.21.1
  requires Java 21. Check with `java -version` in a terminal.
- **Gradle** is *not* required to be installed globally if you generate the
  wrapper as below, but having one handy makes step 2 easier. You can grab
  it from [gradle.org](https://gradle.org/install/) or via a package
  manager (`choco install gradle`, `scoop install gradle`).

### 2. Generate the Gradle wrapper (one-time)

This project ships without a pre-built `gradlew.bat`/wrapper jar (those are
binary files). Generate them once, from the project root, in PowerShell or
cmd:

```powershell
gradle wrapper --gradle-version 8.8
```

This creates `gradlew.bat`, `gradlew`, and `gradle/wrapper/gradle-wrapper.jar`.
From then on, use `gradlew.bat` instead of a system-wide `gradle` so everyone
building the project uses the exact same Gradle version.

> Alternative: open the project folder in **IntelliJ IDEA** with the
> "Gradle" and (optionally) "Minecraft Development" plugins installed.
> IntelliJ will detect `build.gradle`, prompt to import, and handle the
> wrapper/dependency download for you — no manual `gradle wrapper` step
> needed.

### 3. Build the JAR

```powershell
.\gradlew.bat build
```

First run will download Minecraft 1.21.1, Yarn mappings, Fabric Loader, and
Fabric API — this can take a few minutes and needs an internet connection.

The finished mod JAR appears at:

```
build\libs\astral-cat-client-1.0.0.jar
```

(There will also be a `-sources.jar`; that one's just the source, not
needed to run the mod.)

### 4. Install it

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for
   Minecraft 1.21.1 if you haven't already.
2. Download **Fabric API** for 1.21.1 from Modrinth/CurseForge and drop it
   in `.minecraft/mods/`.
3. Drop `astral-cat-client-1.0.0.jar` into the same `mods` folder.
4. Launch the Fabric profile. Open the title screen — cats should fall.

### 5. (Optional) Run it in a dev environment instead

```powershell
.\gradlew.bat runClient
```

This launches a throwaway dev instance of Minecraft with the mod already
loaded, useful while you're tweaking values.

## Tweaking it

Edit `.minecraft/config/astralcatclient.json` while the game is closed (or
reopen the file after first launch so defaults exist):

```json
{
  "enabled": true,
  "spawnRate": 0.35,
  "fallSpeed": 1.0,
  "catScale": 1.0,
  "maxCats": 150,
  "onTitleScreen": true,
  "onPauseScreen": true
}
```

- `spawnRate` — average new cats spawned per client tick. Try `1.5` for a
  denser blizzard of cats.
- `fallSpeed` / `catScale` — multipliers, `1.0` is the default.
- `maxCats` — hard ceiling so it can't eat your framerate.


## Christmas Cat Edition

The title and pause menus feature a subtle winter-sky tint, animated pixel snow, festive falling cats, and icy cat-ear frames around menu buttons. These are client-side visual effects only.
