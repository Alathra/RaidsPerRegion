package io.github.alathra.raidsperregion.raid;

import java.time.Instant;
import java.util.*;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import io.github.alathra.raidsperregion.RaidsPerRegion;
import io.github.alathra.raidsperregion.config.Settings;
import io.github.alathra.raidsperregion.raid.preset.RaidPreset;
import io.github.alathra.raidsperregion.raid.tier.RaidTier;
import io.github.milkdrinkers.colorparser.ColorParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.lumine.mythic.core.mobs.ActiveMob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Raid {
    // -- PLUGIN INSTANCE --
    private final RaidsPerRegion plugin;

    // -- PARAMETERS --
    // Used to identify raids
    private final UUID uuid;
    // The person who started the raid
    private final OfflinePlayer starter;
    // The world that the raid is happening in
    private final World world;
    // The mobs and spawn chances
    private final RaidPreset preset;
    // The tier of the raid, which stores relevant settings
    private final RaidTier tier;
    // The target area (region, town, etc.)
    private final RaidArea area;

    // -- POST-START VARIABLES --
    // The exact point in time the raid has started
    private Instant startTime;
    // The number of seconds remaining in the raid
    private int secondsLeft;
    // The ActiveMob (essentially entity) of the boss. Set when the boss is spawned
    private ActiveMob bossMob;
    // If the boss has been spawned (if applicable)
    private boolean isBossSpawned;
    // If the boss entity has been killed
    private boolean isBossKilled;

    // List of players who participate in a raid (defined as entering the raid region).
    // Integer is the amount of mob kills by the player
    private Table<UUID, Integer, Sidebar> participantsTable;
    // List of players who are actively participating in the raid. Same as
    // participants except when they leave the region they are removed from the list.
    private ArrayList<UUID> activeParticipants = new ArrayList<>();

    // Mobs added as they spawn
    private HashSet<ActiveMob> mobs = new HashSet<>();

    public Raid(@NotNull OfflinePlayer starter, @NotNull World world, @NotNull RaidPreset preset, @NotNull RaidTier tier, @NotNull RaidArea area) {
        plugin = RaidsPerRegion.getInstance();
        uuid = UUID.randomUUID();
        this.starter = starter;
        this.world = world;
        this.preset = preset;
        this.tier = tier;
        this.area = area;

        // defaults
        participantsTable = HashBasedTable.create();
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
        SidebarComponent title = SidebarComponent.staticLine(parseMessage("<dark_red><bold> Tier <raid_tier> Raid - <raid_area>", player));
        SidebarComponent lines = SidebarComponent.builder()
            .addStaticLine(ColorParser.of("<dark_red><bold>----------------").build())
            .addDynamicLine(() -> parseMessage("<gold>Time Left: <raid_time_left>"))
            .addDynamicLine(() -> parseMessage("<gold>Kills Goal: <raid_kills_goal>"))
            .addDynamicLine(() -> parseMessage("<gold>Kills Goal: <raid_total_kills>"))
            .addStaticLine(ColorParser.of("<dark_red><bold>----------------").build())
            .build();
        return new ComponentSidebarLayout(title, lines);
    }

    public void addParticipantAndInitiateScoreboard(Player player) {
        participantsTable.put(player.getUniqueId(), 0, null);
        Sidebar sidebar = plugin.getScoreboardLibrary().createSidebar();
        sidebar.addPlayer(player);
        generateScoreboardLayout(player).apply(sidebar);
        participantsTable.put(player.getUniqueId(), 0, sidebar);
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
            int randIndex = (int) (Math.random() * activeParticipants.size());
            UUID playerUUID = activeParticipants.get(randIndex);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
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
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parseMessage(globalCmd).toString());
        }

        for (String perPlayerCmd : Settings.getRaidWinPerParticipantCommands()) {
            for (UUID playerUUID : this.participantsTable.rowKeySet()) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
                if (offlinePlayer.isOnline()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parseMessage(perPlayerCmd, offlinePlayer).toString());
                }
            }
        }
    }

    public void runLossCommands() {
        for (String globalCmd : Settings.getRaidLossGlobalCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parseMessage(globalCmd).toString());
        }

        for (String perPlayerCmd : Settings.getRaidLossPerParticipantCommands()) {
            for (UUID playerUUID : this.participantsTable.rowKeySet()) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
                if (offlinePlayer.isOnline()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parseMessage(perPlayerCmd, offlinePlayer).toString());
                }
            }
        }
    }

    private Component parseMessage(String raw) {
        return ColorParser.of(raw)
            .parseLegacy()
            .parseMinimessagePlaceholder("<raid_tier>", tier.getName())
            .parseMinimessagePlaceholder("<raid_area_name>", area.getName())
            .parseMinimessagePlaceholder("<raid_area_type>", area.getName())
            .parseMinimessagePlaceholder("<raid_starter>", starter.getName())
            .parseMinimessagePlaceholder("<raid_time_left>", getFormattedTimeLeft())
            .parseMinimessagePlaceholder("<raid_kills_goal>", String.valueOf(tier.getKillsGoal()))
            .parseMinimessagePlaceholder("<raid_total_kills>", String.valueOf(getTotalKills()))
            .parseMinimessagePlaceholder("<boss_name>", preset.getBoss())
            .build();
    }

    private Component parseMessage(String raw, OfflinePlayer participant) {
        return ColorParser.of(raw)
            .parseLegacy()
            .parseMinimessagePlaceholder("<raid_tier>", tier.getName())
            .parseMinimessagePlaceholder("<raid_area_name>", area.getName())
            .parseMinimessagePlaceholder("<raid_area_type>", area.getName())
            .parseMinimessagePlaceholder("<raid_starter>", starter.getName())
            .parseMinimessagePlaceholder("<raid_time_left>", getFormattedTimeLeft())
            .parseMinimessagePlaceholder("<raid_kills_goal>", String.valueOf(tier.getKillsGoal()))
            .parseMinimessagePlaceholder("<raid_total_kills>", String.valueOf(getTotalKills()))
            .parseMinimessagePlaceholder("<raid_participant_name>", participant.getName())
            .parseMinimessagePlaceholder("<raid_participant_kills>", String.valueOf(getKills(participant.getUniqueId())))
            .parseMinimessagePlaceholder("<boss_name>", preset.getBoss())
            .build();
    }

    // Abstract methods
    public abstract void startRaid(CommandSender sender, boolean isConsole);

    public abstract void stopRaid();

    public abstract void findParticipants();

    public abstract void spawnMobForSpecificParticipant(int distanceFactor, Player player);

    public abstract void onRaidTimer();

    public abstract void onRaidWin();

    public abstract void onRaidLoss();

    public abstract boolean spawnBoss(int distanceFactor);

    public abstract void forceMobsSpawning();

    public abstract void resetMobsSpawning();

    // Getters and Setters

    public UUID getUUID() {
        return uuid;
    }

    public OfflinePlayer getStarter() {
        return starter;
    }

    public World getWorld() {
        return world;
    }

    public RaidPreset getPreset() {
        return preset;
    }

    public RaidTier getTier() {
        return tier;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Table<UUID, Integer, Sidebar> getParticipantsTable() {
        return participantsTable;
    }

    public ArrayList<UUID> getActiveParticipants() {
        return activeParticipants;
    }

    public HashSet<ActiveMob> getMobs() {
        return mobs;
    }


}