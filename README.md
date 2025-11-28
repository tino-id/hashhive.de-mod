# HashHive ChatGame Solver

A Minecraft Fabric mod for version 1.21.10 that automatically solves ChatGame equations and displays the solution locally.

## Features

- **Automatic detection** of ChatGame events
- **Solves mathematical equations** with `+`, `-`, and `*` operators
- **10-minute countdown timer** displayed in the top-left corner (white text with black background)
- **Local-only solution display** in your chat (red bold text)
- **Auto-send option** - automatically submits the solution after 1.5-3.5 seconds (randomized)
- **Correct operator precedence** (multiplication before addition/subtraction)
- **Chat filter** - suppresses "You can't mine this block!" messages

## Requirements

- Minecraft 1.21.10
- [Fabric Loader](https://fabricmc.net/use/) 0.18.1+
- [Fabric API](https://modrinth.com/mod/fabric-api) 0.138.3+
- Java 21

## Installation

1. Make sure you have [Fabric Loader](https://fabricmc.net/use/) and [Fabric API](https://modrinth.com/mod/fabric-api) installed
2. Download the latest release JAR from the [Releases page](https://github.com/tino-id/hashhive.de-mod/releases) or build it yourself
3. Copy the JAR file to your Minecraft `mods` folder
4. Launch Minecraft with the Fabric profile

## Usage

The mod works fully automatically and is **enabled by default**. When a ChatGame event appears in chat:

```
ChatGame Event
Solve this equation and get rewards!
Equation: 6 * 9
```

You will automatically see in your chat (only visible to you):
```
[HashHive] Solution: 54
```

A countdown timer will also appear in the top-left corner showing the time to the next ChatGame:
```
ChatGame: 10:00
```

### Commands

All commands are under `/hh`:

| Command | Description |
|---------|-------------|
| `/hh` | Show full status (mod + auto-send) |
| `/hh toggle` | Toggle the mod on/off |
| `/hh enable` | Enable the mod |
| `/hh disable` | Disable the mod |
| `/hh auto` | Show auto-send status |
| `/hh auto toggle` | Toggle auto-send on/off |
| `/hh auto enable` | Enable auto-send |
| `/hh auto disable` | Disable auto-send |
| `/hh help` | Show all commands |

**Auto-send**: When enabled, the solution is automatically sent to the server after a random delay of 1.5-3.5 seconds.

When the mod is disabled, it will not detect ChatGame events, solve equations, or display the countdown timer.

## Building from Source

```bash
# Build the mod
./gradlew build

# Launch Minecraft test client
./gradlew runClient

# Clean build
./gradlew clean build
```

The compiled JAR will be located at `build/libs/hashhive-<version>.jar`.