# FreecamPlus

A Fabric mod for Minecraft that adds a **freecam** — a free-flying camera you can detach from your
character to fly around and look at your build or the world, without moving or exposing your actual
player.

Created by **_Flex_Sky**.

## Features

- **Toggle or Hold activation** — choose whether the freecam key toggles freecam on/off, or only
  stays active while held down.
- **Fully rebindable key** — change the freecam key from the normal Minecraft Controls menu, or from
  the in-game FreecamPlus settings screen (if [Mod Menu](https://modrinth.com/mod/modmenu) is
  installed). Bind it to `Y`, `Space`, or anything else you like. Defaults to `R`.
- **Adjustable fly speed** — cycle through speed multipliers in the settings screen; hold Sprint to
  go even faster.
- **Frozen, safe player** — while freecam is active your character stops moving, takes no fall
  damage, and can't attack, mine, or interact with anything, so other players just see you standing
  still.
- Works out of the box, with an optional [Mod Menu](https://modrinth.com/mod/modmenu) config screen.

## How it works

Freecam detaches the render camera from your player and moves it independently using your normal
movement keys (WASD, Space, Shift) and mouse look. Your player entity is frozen in place (no
movement, no gravity, no interactions) while the camera flies around — it does not fly around for
real, so it can only see already-loaded chunks around your actual position (the same as any
client-side camera mod).

## Requirements

- Minecraft **1.21.11**
- [Fabric Loader](https://fabricmc.net/use/) `0.19.0+`
- [Fabric API](https://modrinth.com/mod/fabric-api)
- Java 21
- Optional: [Mod Menu](https://modrinth.com/mod/modmenu) for the in-game settings screen

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.11.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) for 1.21.11 and put it in your `mods` folder.
3. Download `freecamplus-<version>.jar` from the [Releases](../../releases) page and put it in your `mods` folder.
4. (Optional) Install [Mod Menu](https://modrinth.com/mod/modmenu) to get an in-game settings screen.

## Usage

- Press the freecam key (default **R**) to toggle freecam, or hold it if Hold mode is selected.
- Move with **WASD**, fly up/down with **Space** / **Shift**, and hold **Sprint** (default Ctrl) to
  move faster.
- Change the key or activation mode from **Options > Controls** or from **Mod Menu > FreecamPlus**.

## Building from source

```
./gradlew build
```

The built jar will be in `build/libs/`.

## License

[MIT](LICENSE)
