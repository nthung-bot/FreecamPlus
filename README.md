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
- **Smooth camera** — the freecam is interpolated every frame so movement stays fluid even at lower
  tick rates. Optional **Smooth Movement** easing gives it gentle acceleration/deceleration.
- **Easy speed control** — scroll the mouse wheel while in freecam to change fly speed on the fly,
  pick a speed multiplier in the settings screen, and hold **Sprint** to move faster.
- **Fullbright** — optionally light up the world fully while in freecam so dark caves and builds are
  easy to see.
- **Waypoints (like a minimap)** — press the waypoint key (default **B**) in freecam to open a
  **Create Waypoint** dialog: type a name and pick a color (including a **custom hex color**). Each
  waypoint is drawn in the world with its name and a **live distance readout in m / km**. Drop as
  many as you like, and clear them all from the dialog.
- **Frozen, safe player** — while freecam is active your character stops moving, takes no fall
  damage, and can't attack, mine, or interact with anything, so other players just see you standing
  still.
- Works out of the box, with an optional [Mod Menu](https://modrinth.com/mod/modmenu) config screen.

## How it works

Freecam detaches the render camera from your player and moves it independently using your normal
movement keys (WASD, Space, Shift) and mouse look. Your player entity is frozen in place (no
movement, no gravity, no interactions) while the camera flies around — it does not fly around for
real, so it can only see already-loaded chunks around your actual position (the same as any
client-side camera mod). Flying past your render distance shows unloaded terrain; raise your render
distance if you want to roam farther.

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
- **Scroll the mouse wheel** while flying to change the fly speed.
- Press the waypoint key (default **B**) to open the **Create Waypoint** dialog (name + color).
- Change keys, activation mode, speed, Smooth Movement, Fullbright, and the waypoint defaults from
  **Options > Controls** or from **Mod Menu > FreecamPlus** (the **Checkpoint** button opens the
  waypoint defaults/keybind).

## Building from source

```
./gradlew build
```

The built jar will be in `build/libs/`.

## License

[MIT](LICENSE)
