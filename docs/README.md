<h1 style="text-align:center;">RaidsPerRegion4</h1>
<p style="text-align:center;">
    <img alt="GitHub License" src="https://img.shields.io/github/license/Alathra/AlathraPorts?style=for-the-badge&color=blue&labelColor=141417">
    <img alt="GitHub Downloads (all assets, all releases)" src="https://img.shields.io/github/downloads/Alathra/AlathraPorts/total?style=for-the-badge&labelColor=141417">
    <img alt="GitHub Release" src="https://img.shields.io/github/v/release/Alathra/AlathraPorts?include_prereleases&sort=semver&style=for-the-badge&label=LATEST%20VERSION&labelColor=141417">
    <img alt="GitHub Actions Workflow Status" src="https://img.shields.io/github/actions/workflow/status/Alathra/AlathraPorts/ci.yml?style=for-the-badge&labelColor=141417">
    <img alt="GitHub Issues or Pull Requests" src="https://img.shields.io/github/issues/Alathra/AlathraPorts?style=for-the-badge&labelColor=141417">
    <img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/Alathra/AlathraPorts?style=for-the-badge&labelColor=141417">
</p>
<p align="center"><img src="https://raw.githubusercontent.com/Alathra/RaidsPerRegion/refs/heads/main/docs/assets/raids_per_region_logo.png?token=GHSAT0AAAAAACX63Z3ZL7HZEYRWXWC4X2AOZ6VWFMA" alt="RaidsPerRegion" /></p>

---


## Description
RaidsPerRegion adds a customizable PvE (Player v.s. Enviornment) server event/minigame that takes inspiration from plugins like [MobArena](https://www.spigotmc.org/resources/mobarena.34110) and the vanilla Minecraft pillager raids. The name "RaidsPerRegion" refers to the fact that it adds dynamic mob "Raids" that can be started via commands in areas, or "Regions". The word "region" here is a reference to a [WorldGuard](https://dev.bukkit.org/projects/worldguard) region, which is the standard raid area type supported by the plugin. Additional (optional) raid types are also supported for other plugins such as [Towny](https://www.spigotmc.org/resources/towny-advanced.72694/) and [Factions](https://www.spigotmc.org/resources/factionsuuid.1035/). RaidsPerRegion **does not support vanilla mobs** and instead uses [MythicMobs](https://www.spigotmc.org/resources/%E2%9A%94-mythicmobs-free-version-%E2%96%BAthe-1-custom-mob-creator%E2%97%84.5702/) as a base for for defining Raid mobs.

---

## Dependencies/Hooks
### Dependencies
* #### [MythicMobs](https://www.spigotmc.org/resources/%E2%9A%94-mythicmobs-free-version-%E2%96%BAthe-1-custom-mob-creator%E2%97%84.5702/)
* #### [WorldGuard](https://dev.bukkit.org/projects/worldguard) (requires [WorldEdit](https://modrinth.com/plugin/worldedit/versions) or [FastAsyncWorldEdit](https://www.spigotmc.org/resources/fastasyncworldedit.13932/))
### Optional Hooks
* #### [Factions](https://www.spigotmc.org/resources/factionsuuid.1035/)
    Adds the ability to start raids on Factions.
* #### [KingdomsX](https://www.spigotmc.org/resources/kingdomsx.77670/)
    Adds the ability to start raids on Kingdoms.
* #### [Towny](https://www.spigotmc.org/resources/towny-advanced.72694/)
    Adds the ability to start raids on Towns.

---

## Oveview
### What is a Raid?
A RaidsPerRegion "Raid" is a timed server event/minigame where players must kill a certain amount of mobs before the timer runs out or they will lose the raid. The Raids itself is more of a template, so how they are used and the purpose they serve is entirely up to the admins or gameplay designers of your Minecraft server. On the [Alathra](https://alathra.com/) server for example, Raids are executed randomly on player settlements ([Towny](https://www.spigotmc.org/resources/towny-advanced.72694/) towns) in order to introduce a fun PvE challenge and provide players unique items through mob drops and Raid rewards. **RaidsPerRegion is not designed to be plug-and-play software, and should be configured to achieve the best results**. Fortunately, Raids are composed of dynamic and extendable components, making them relatively easy to modify to meet your specific needs.

#### Raid Presets
Raid Presets define the what mobs will spawn in the raid and their details. This includes things such as mob levels, spawn chances/weights and an optional boss mob. This allows you to create infinite "kinds" of raids, with one being selected when starting a new raid. Presets are pre-defined in the [config.yml](https://github.com/Alathra/RaidsPerRegion/blob/main/src/main/resources/config.yml).

#### Raid Tiers
Raid Tiers define how hard the raid will be as well as general raid parmeters. This is where the time limit, kills goal (mobs kills needed to win), spawn rates and other settings can be found. This allows you to create infinite "levels" of raids. Tiers are pre-defined in the [config.yml](https://github.com/Alathra/RaidsPerRegion/blob/main/src/main/resources/config.yml).

#### Raid Scheduler

Raids can be scheduled to execute at set number of minutes from when the start command is run. Announcement messages can be configured in the [config.yml](https://github.com/Alathra/RaidsPerRegion/blob/main/src/main/resources/config.yml) that can be used to warn players before a raid starts. Since Raids can be started by console, it is possible to schedule raids via a scheduled task on your server pannel or server's operating system.

---

## Permissions & Commands
### Permissions
RaidsPerRegion adds the following permission node(s):
* ``raidsperregion.admin``
Grants the user the ability to start, stop and list raids
## Commands
RaidsPerRegion adds the following commands:
* ``/raid start [area_type] [world] [area_name] <raid_preset> <raid_tier> <scheduled_minutes>``
Starts a raid with the given parameters. [] indiccates a required argument and <> indicates an optional argument.
Selecting "random" for the area_name will pick a random area of the defined type in the defined world.
* ``/raid stop [area_name]``
Stops an ongoing or scheduled raid.
* ``/raid list``
Lists all ongoing and scheduled raids.

