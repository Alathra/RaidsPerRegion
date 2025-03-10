package io.github.alathra.raidsperregion.raid;

import java.util.*;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import io.github.alathra.raidsperregion.RaidsPerRegion;
import io.github.alathra.raidsperregion.config.Settings;
import io.github.alathra.raidsperregion.raid.area.RaidArea;
import io.github.alathra.raidsperregion.raid.preset.RaidPreset;
import io.github.alathra.raidsperregion.raid.tier.RaidTier;
import io.github.alathra.raidsperregion.utility.Logger;
import io.github.alathra.raidsperregion.utility.MythicMobsUtil;
import io.github.alathra.raidsperregion.utility.RandomUtil;
import io.github.milkdrinkers.colorparser.ColorParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Raid {
    // -- PLUGIN INSTANCE --
    private final RaidsPerRegion plugin;

    // -- PARAMETERS --
    // Used to identify raids
    private final UUID uuid;
    // The person who started the raid
    private final CommandSender starter;
    // The world that the raid is happening in
    private final World world;
    // The mobs and spawn chances
    private final RaidPreset preset;
    // The tier of the raid, which stores relevant settings
    private final RaidTier tier;
    // The target area (region, town, etc.)
    private final RaidArea area;

    // -- POST-START VARIABLES --
    // The number of seconds remaining in the raid
    private int secondsLeft;
    // The ActiveMobs (entities) of the spawned mobs in the raid
    private final Set<ActiveMob> mobs;
    // The ActiveMob (essentially entity) of the boss. Set when the boss is spawned
    private ActiveMob bossMob;
    // If the boss has been spawned (if applicable)
    private boolean isBossSpawned;
    // If the boss entity has been killed
    private boolean isBossKilled;
    // Amount of player deaths in the raid
    private int playerDeaths;

    // -- TASKS --
    private BukkitTask spawnMobsTimer;
    private BukkitTask raidTimer;

    // List of players who participate in a raid (defined as entering the raid region).
    // Integer is the amount of mob kills by the player
    private final Table<UUID, Integer, Sidebar> participantsTable;
    // Set of players who are actively participating in the raid. Same as
    // participants except when they leave the region they are removed from the list.
    private final Set<UUID> activeParticipants;

    public Raid(@NotNull CommandSender starter, @NotNull World world, @NotNull RaidArea area, @NotNull RaidPreset preset, @NotNull RaidTier tier) {
        plugin = RaidsPerRegion.getInstance();
        uuid = UUID.randomUUID();
        this.starter = starter;
        this.world = world;
        this.preset = preset;
        this.tier = tier;
        this.area = area;

        // defaults
        participantsTable = HashBasedTable.create();
        activeParticipants = new HashSet<>();
        mobs = new HashSet<>();
        isBossSpawned = false;
        isBossKilled = false;
        secondsLeft = tier.getTimeLimit();
    }

    public int getTotalKills() {
        return participantsTable.columnKeySet().stream().mapToInt(Integer::intValue).sum();
    }

    public @Nullable Integer getKills(UUID playerUUID) {
        // Check if the UUID exists in the table's row keys
        if (participantsTable.containsRow(playerUUID)) {
            // Return the first Integer value from the column keys for the given UUID
            return participantsTable.row(playerUUID).keySet().stream().findFirst().orElse(null);
        }
        // If the UUID doesn't exist, return null
        return null;
    }

    public @Nullable Sidebar getSidebar(UUID playerUUID) {
        // Check if the UUID exists in the table's row keys
        if (participantsTable.containsRow(playerUUID)) {
            // Return the first Sidebar object from the values collection
            return participantsTable.row(playerUUID).values().stream().findFirst().orElse(null);
        }
        // If the UUID doesn't exist, return null
        return null;
    }

    public void sendTitleToParticipants(String titleRaw, String subtitleRaw) {
        for (UUID playerUUID : participantsTable.rowKeySet()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
            if (offlinePlayer.isOnline()) {
                Player player = (Player) offlinePlayer;
                sendTitleToPlayer(player, titleRaw, subtitleRaw);
            }
        }
    }

    public void sendTitleToPlayer(Player player, String titleRaw, String subtitleRaw) {
        player.showTitle(Title.title(parseMessage(titleRaw, player), parseMessage(subtitleRaw, player)));
    }

    public ComponentSidebarLayout generateScoreboardLayout(OfflinePlayer player) {
        Component titleComponent = parseMessage(Settings.getScoreboardTitle(), player);
        SidebarComponent title = SidebarComponent.staticLine(titleComponent);
        SidebarComponent.Builder lineBuilder = SidebarComponent.builder();
        for (String line : Settings.getScoreboardLines()) {
            lineBuilder.addDynamicLine(() -> parseMessage(line, player));
        }
        return new ComponentSidebarLayout(title, lineBuilder.build());
    }

    public void addParticipantAndInitiateScoreboard(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) {
            return;
        }
        Sidebar sidebar = plugin.getScoreboardLibrary().createSidebar();
        if (Settings.isScoreboardEnabled())
            sidebar.addPlayer(player);
        participantsTable.put(playerUUID, 0, sidebar);
        if (Settings.isScoreboardEnabled())
            generateScoreboardLayout(player).apply(sidebar);
        participantsTable.put(playerUUID, 0, sidebar);
    }

    public void updateScoreboards() {
        for (UUID playerUUID : participantsTable.rowKeySet()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
            if (offlinePlayer.isOnline()) {
                Player player = offlinePlayer.getPlayer();
                Sidebar sidebar = getSidebar(playerUUID);
                if (sidebar != null) {
                    generateScoreboardLayout(player).apply(sidebar);
                }
            }
        }
    }

    public void removeScoreboards() {
        for (UUID playerUUID : participantsTable.rowKeySet()) {
            Sidebar sidebar = getSidebar(playerUUID);
            if (sidebar != null) {
                sidebar.close();
            }
        }
    }

    public String getFormattedTimeLeft() {
        int hours = secondsLeft / 3600;
        int minutes = (secondsLeft % 3600) / 60;
        int seconds = secondsLeft % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void spawnMobsForAllActiveParticipants(int distanceFactor) {
        for (UUID playerUUID : activeParticipants) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
            if (offlinePlayer.isOnline()) {
                Player player = (Player) offlinePlayer;
                spawnMobForSpecificParticipant(distanceFactor, player);
            }
        }
    }

    public void spawnMobsForCycle(int distanceFactor) {
        for (int i = 0; i < tier.getMobSpawnsPerCycle(); i++) {
            if (activeParticipants.isEmpty()) {
                return;
            }
            UUID randomPlayerUUID = RandomUtil.getRandomElementInSet(activeParticipants);
            // Set was empty
            if (randomPlayerUUID == null) {
                return;
            }
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(randomPlayerUUID);
            if (offlinePlayer.isOnline()) {
                Player player = (Player) offlinePlayer;
                spawnMobForSpecificParticipant(distanceFactor, player);
            }
        }
    }

    public void cleanStoredMobs() {
        Iterator<ActiveMob> iterator = mobs.iterator();
        while (iterator.hasNext()) {
            ActiveMob mob = iterator.next();
            if (mob == null) {
                iterator.remove();
            } else if (mob.isDead()) {
                iterator.remove();
            }
        }
    }

    public void runWinCommands() {
        for (String globalCmd : Settings.getRaidWinGlobalCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlainTextComponentSerializer.plainText().serialize(parseMessage(globalCmd)));
        }

        for (String perPlayerCmd : Settings.getRaidWinPerParticipantCommands()) {
            for (UUID playerUUID : this.participantsTable.rowKeySet()) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
                if (offlinePlayer.isOnline()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlainTextComponentSerializer.plainText().serialize(parseMessage(perPlayerCmd, offlinePlayer)));
                }
            }
        }
    }

    public void runLossCommands() {
        for (String globalCmd : Settings.getRaidLossGlobalCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlainTextComponentSerializer.plainText().serialize(parseMessage(globalCmd)));
        }

        for (String perPlayerCmd : Settings.getRaidLossPerParticipantCommands()) {
            for (UUID playerUUID : this.participantsTable.rowKeySet()) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
                if (offlinePlayer.isOnline()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlainTextComponentSerializer.plainText().serialize(parseMessage(perPlayerCmd, offlinePlayer)));
                }
            }
        }
    }

    public void updateParticipants() {
        Set<UUID> playersInArea = area.findPlayersInArea();

        // Find and add new participants
        for (UUID playerUUID : playersInArea) {
            activeParticipants.add(playerUUID);
            if (!participantsTable.rowKeySet().contains(playerUUID)) {
                addParticipantAndInitiateScoreboard(playerUUID);
                Player player = Bukkit.getPlayer(playerUUID);
                if (player == null) {
                    continue;
                }
                sendTitleToPlayer(player, Settings.getRaidStartTitle(), Settings.getRaidStartSubtitle());
            }
        }
        // Purge active participants not in raid area
        activeParticipants.removeIf(playerUUID -> !playersInArea.contains(playerUUID));
    }

    public void start() {
        // Impossible to spawn mobs because unable to force mob spawning in area
        if (!Settings.forceMobSpawningInRegionOnRaidStart() && area.wasMobSpawningEnabledBeforeRaid()) {
            Logger.get().warn("<red>Failed to start raid: Mob spawning could not be forced in <raid_area_name> due to config setting");
            starter.sendMessage(parseMessage("<red>Failed to start raid: Mob spawning could not be forced in <raid_area_name> due to config setting"));
            return;
        }
        if (!RaidManager.registerRaid(this)) {
            Logger.get().warn("<red>Failed to start raid: There is already an ongoing raid in <raid_area_name>");
            starter.sendMessage(parseMessage("<red>Failed to start raid: There is already an ongoing raid in <raid_area_name>"));
        }
        Logger.get().info(parseMessage("<green>You have started a tier <raid_tier> raid on <raid_area_name>"));
        starter.sendMessage(parseMessage("<green>You have started a tier <raid_tier> raid on <raid_area_name>"));

        // see if mob spawning is allowed in region, and force it on if necessary
        if (Settings.forceMobSpawningInRegionOnRaidStart()) {
            if(!area.forceMobSpawning()) {
                Logger.get().warn("<red>Failed to start raid: Mob spawning could not be forced in <raid_area_name> due to an internal error");
                starter.sendMessage(parseMessage("<red>Failed to start raid: Mob spawning could not be forced in <raid_area_name> due to an internal error"));
                stop(); // cancel the raid
                RaidManager.deRegisterRaid(this);
            }
        }

        raidTimer = plugin.getServer().getScheduler().runTaskTimer(plugin, this::onRaidTimer, 0L, 20L); // Runs instantly, repeats every second
        spawnMobsTimer = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            spawnMobsForCycle(20);
        }, 0L, (long) (20.0 / tier.getCycleRate()));

    }

    public void onRaidTimer() {
        secondsLeft--;
        updateParticipants();
        if (Settings.isScoreboardEnabled())
            updateScoreboards();
        cleanStoredMobs();

        // check for win
        if (getTotalKills() >= tier.getKillsGoal()) {
            onWin();
            return;
        }

        // check for loss
        if (secondsLeft <= 0) {
            onLoss();
            return;
        }
    }

    public void stop() {
        clearSpawnedMobs();
        closeOut();
        if (Settings.areTitleMessagesEnabled())
            sendTitleToParticipants(Settings.getRaidCancelTitle(), Settings.getRaidCancelSubtitle());
    }

    public void clearSpawnedMobs() {
        for (ActiveMob mob : mobs) {
            if (mob != null && !mob.isDead()) {
                mob.remove();
            }
        }
        mobs.clear();
    }

    public void onWin() {
        // check for boss condition
        if (preset.hasBoss()) {
            if (!isBossSpawned) {
                spawnBoss(10);
                return;
            }
            if (!isBossKilled) {
                return;
            }
        }

        if (Settings.areRaidResultConsoleCommandsEnabled())
            this.runWinCommands();

        if (Settings.areTitleMessagesEnabled())
            sendTitleToParticipants(Settings.getRaidWinTitle(), Settings.getRaidWinSubtitle());
        
        clearSpawnedMobs();
        closeOut();
    }

    public void onLoss() {

        if (Settings.clearMobsOnRaidLoss())
            clearSpawnedMobs();

        if (Settings.areTitleMessagesEnabled())
            sendTitleToParticipants(Settings.getRaidLoseTitle(), Settings.getRaidLoseSubtitle());

        if (Settings.areRaidResultConsoleCommandsEnabled())
            runLossCommands();

        closeOut();
    }

    public void spawnMobForSpecificParticipant(int distanceFactor, Player player) {

        // If raid mobs already spawned is at the limit, do not spawn
        if (mobs.size() >= getTier().getMaxMobsAtOnce()) {
            return;
        }

        final int distanceFactorSubtractionPerSpawn = distanceFactor / 10;

        // 10 attempts to spawn a mob
        for (int i = 0; i < 10; i++) {
            if (distanceFactor < 0) {
                // failed to spawn mob after multiple attempts, give up. They are most likely at
                // the edge of the region
                break;
            }
            // GET RANDOM DISTANCE FROM PLAYER

            double radius = distanceFactor * Math.random() + 10;
            // *20+10: random number between 10 and 30
            double angle = Math.toRadians(360 * Math.random());

            // x and z as distances
            int x = (int) (Math.sin(angle) * radius);
            int z = (int) (Math.cos(angle) * radius);

            // ADD DISTANCE TO PLAYER LOCATION TO PRODUCE RAND LOCATION
            x = player.getLocation().getBlockX() + x;
            z = player.getLocation().getBlockZ() + z;

            // CALCULATE Y FOR LOCATION
            // start with player's y location -5
            int minY = player.getLocation().getBlockY() - 10;
            int maxY = player.getLocation().getBlockY() + 10;
            int y = -65;

            for (int testY = minY; testY < maxY; testY++) {
                Location potentialSpawnLoc = new Location(world, x, testY, z);
                if (!potentialSpawnLoc.getBlock().getType().isSolid()) {
                    continue;
                }
                potentialSpawnLoc.setY(testY + 1);
                if (!potentialSpawnLoc.getBlock().getType().equals(Material.AIR)) {
                    continue;
                }
                potentialSpawnLoc.setY(testY + 2);
                if (!potentialSpawnLoc.getBlock().getType().equals(Material.AIR)) {
                    continue;
                }
                potentialSpawnLoc.setY(testY + 3);
                if (!potentialSpawnLoc.getBlock().getType().equals(Material.AIR)) {
                    continue;
                }
                y = testY;
                break;
            }

            if (y == -65) {
                continue;
            }

            // Get block above spawnpoint so mob does not spawn in the ground
            y++;

            // if mob cannot spawn inside the region, try again with smaller radius
            Location spawnLocation = new Location(world, x, y, z);
            if (!area.containsLocation(spawnLocation)) {
                distanceFactor -= distanceFactorSubtractionPerSpawn;
                continue;
            }

            // spawn mob
            ActiveMob mob = MythicMobsUtil.spawnRandomMob(preset, spawnLocation);
            if (mob == null || mob.isDead()) {
                return;
            }
            mobs.add(mob);
            // break out of loop, mob spawned
            return;
        }
    }

    public void spawnBoss(int distanceFactor) {
        // GET RANDOM PLAYER FROM ACTIVE PARTICIPANTS

        if (this.getActiveParticipants().isEmpty()) {
            return;
        }

        UUID randomPlayerUUID = RandomUtil.getRandomElementInSet(activeParticipants);
        if (randomPlayerUUID == null) {
            return;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(randomPlayerUUID);
        Player player;
        if (!offlinePlayer.isOnline()) {
            return;
        } else {
            player = (Player) offlinePlayer;
        }

        // If raid mobs already spawned is at the limit, do not spawn
        if (mobs.size() >= tier.getKillsGoal()) {
            return;
        }

        final int distanceFactorSubtractionPerSpawn = distanceFactor / 10;

        // 10 attempts to spawn a mob
        for (int i = 0; i < 10; i++) {
            if (distanceFactor < 0) {
                // failed to spawn mob after multiple attempts, give up. They are most likely at
                // the edge of the region
                break;
            }
            // GET RANDOM DISTANCE FROM PLAYER

            double radius = distanceFactor * Math.random() + 10;
            // *20+10: random number between 10 and 30
            double angle = Math.toRadians(360 * Math.random());

            // x and z as distances
            int x = (int) (Math.sin(angle) * radius);
            int z = (int) (Math.cos(angle) * radius);

            // ADD DISTANCE TO PLAYER LOCATION TO PRODUCE RAND LOCATION
            x = player.getLocation().getBlockX() + x;
            z = player.getLocation().getBlockZ() + z;

            // CALCULATE Y FOR LOCATION
            // start with player's y location -5
            int minY = player.getLocation().getBlockY() - 10;
            int maxY = player.getLocation().getBlockY() + 10;
            int y = -65;

            for (int testY = minY; testY < maxY; testY++) {
                Location potentialSpawnLoc = new Location(world, x, testY, z);
                if (!potentialSpawnLoc.getBlock().getType().isSolid()) {
                    continue;
                }
                potentialSpawnLoc.setY((int) testY + 1);
                if (!potentialSpawnLoc.getBlock().getType().equals(Material.AIR)) {
                    continue;
                }
                potentialSpawnLoc.setY((int) testY + 2);
                if (!potentialSpawnLoc.getBlock().getType().equals(Material.AIR)) {
                    continue;
                }
                potentialSpawnLoc.setY((int) testY + 3);
                if (!potentialSpawnLoc.getBlock().getType().equals(Material.AIR)) {
                    continue;
                }
                y = testY;
                break;
            }

            if (y == -65) {
                continue;
            }

            // Get block above spawnpoint so mob does not spawn in the ground
            y++;

            // if mob cannot spawn inside the region, try again with smaller radius
            Location spawnLocation = new Location(world, x, y, z);
            if (!area.containsLocation(spawnLocation)) {
                distanceFactor -= distanceFactorSubtractionPerSpawn;
                continue;
            }

            // spawn boss
            bossMob = MythicMobsUtil.spawnMob(preset.getBoss(), spawnLocation);
            if (bossMob == null || bossMob.isDead()) {
                return;
            }

            isBossSpawned = true;
            if (Settings.areTitleMessagesEnabled())
                sendTitleToParticipants(Settings.getRaidBossSpawnTitle(), Settings.getRaidBossSpawnSubtitle());

            // break out of method, boss spawned
            return;
        }

    }

    // Private methods

    public Component parseMessage(String raw) {
        return ColorParser.of(raw)
            .parseLegacy()
            .parseMinimessagePlaceholder("raid_tier", tier.getName())
            .parseMinimessagePlaceholder("raid_area_name", area.getName())
            .parseMinimessagePlaceholder("raid_area_type", area.getName())
            .parseMinimessagePlaceholder("raid_starter", starter.getName())
            .parseMinimessagePlaceholder("raid_time_left", getFormattedTimeLeft())
            .parseMinimessagePlaceholder("raid_kills_goal", String.valueOf(tier.getKillsGoal()))
            .parseMinimessagePlaceholder("raid_total_kills", String.valueOf(getTotalKills()))
            .parseMinimessagePlaceholder("raid_boss_name", preset.getBoss())
            .parseMinimessagePlaceholder("raid_participants_total_deaths", String.valueOf(playerDeaths))
            .parseMinimessagePlaceholder("raid_scoreboard_border", Settings.getScoreboardBorder())
            .build();
    }

    public Component parseMessage(String raw, OfflinePlayer participant) {
        return ColorParser.of(raw)
            .parseLegacy()
            .parseMinimessagePlaceholder("raid_tier", tier.getName())
            .parseMinimessagePlaceholder("raid_area_name", area.getName())
            .parseMinimessagePlaceholder("raid_area_type", area.getName())
            .parseMinimessagePlaceholder("raid_starter", starter.getName())
            .parseMinimessagePlaceholder("raid_time_left", getFormattedTimeLeft())
            .parseMinimessagePlaceholder("raid_kills_goal", String.valueOf(tier.getKillsGoal()))
            .parseMinimessagePlaceholder("raid_total_kills", String.valueOf(getTotalKills()))
            .parseMinimessagePlaceholder("raid_participant_name", participant.getName())
            .parseMinimessagePlaceholder("raid_participant_kills", String.valueOf(getKills(participant.getUniqueId())))
            .parseMinimessagePlaceholder("raid_boss_name", preset.getBoss())
            .parseMinimessagePlaceholder("raid_participants_total_deaths", String.valueOf(playerDeaths))
            .parseMinimessagePlaceholder("raid_scoreboard_border", Settings.getScoreboardBorder())
            .parsePAPIPlaceholders(participant)
            .build();
    }

    private void closeOut() {
        if(!area.resetMobSpawningToDefault())
            Logger.get().warn("Encountered internal error when resetting mob spawning for raid area <raid_area_name>");
        raidTimer.cancel();
        spawnMobsTimer.cancel();
        if (Settings.isScoreboardEnabled())
            removeScoreboards();
        RaidManager.deRegisterRaid(this);
    }

    // Getters and Setters

    public UUID getUUID() {
        return uuid;
    }

    public CommandSender getStarter() {
        return starter;
    }

    public World getWorld() {
        return world;
    }

    public RaidPreset getPreset() {
        return preset;
    }

    public RaidArea getArea() {
        return area;
    }

    public RaidTier getTier() {
        return tier;
    }

    public Table<UUID, Integer, Sidebar> getParticipantsTable() {
        return participantsTable;
    }

    public Set<UUID> getActiveParticipants() {
        return activeParticipants;
    }

    public Set<ActiveMob> getMobs() {
        return mobs;
    }

    public @Nullable ActiveMob getBossMob() {
        return bossMob;
    }

    public boolean hasBossSpawned() {
        return isBossSpawned;
    }

    public boolean hasBossBeenKilled() {
        return isBossKilled;
    }

    public void setBossKilled(boolean isBossKilled) {
        this.isBossKilled = isBossKilled;
    }

    public void addPlayerDeath() {
        playerDeaths++;
    }

}