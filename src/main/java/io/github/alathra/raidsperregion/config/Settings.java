package io.github.alathra.raidsperregion.config;

import io.github.alathra.raidsperregion.raid.mob.RaidMob;
import io.github.alathra.raidsperregion.raid.mob.RaidMobBuilder;
import io.github.alathra.raidsperregion.raid.preset.RaidPreset;
import io.github.alathra.raidsperregion.raid.preset.RaidPresetBuilder;
import io.github.alathra.raidsperregion.raid.tier.RaidTier;
import io.github.alathra.raidsperregion.raid.tier.RaidTierBuilder;
import io.github.alathra.raidsperregion.utility.Cfg;

import java.util.*;

public class Settings {

    public static boolean preventVanillaMobsSpawnInRaids() {
        return Cfg.get().getOrDefault("GlobalRaidSettings.PreventVanillaMobsSpawningInRaids", true);
    }

    public static boolean forceMobSpawningInRegionOnRaidStart() {
        return Cfg.get().getOrDefault("GlobalRaidSettings.ForceMobSpawningInRegionOnRaidStart", true);
    }

    public static boolean keepInventoryOnPlayerDeath() {
        return Cfg.get().getOrDefault("GlobalRaidSettings.KeepInventoryOnPlayerDeath", false);
    }

    public static boolean keepEXPOnPlayerDeath() {
        return Cfg.get().getOrDefault("GlobalRaidSettings.KeepEXPOnPlayerDeath", false);
    }

    public static boolean disablePvPInRaids() {
        return Cfg.get().getOrDefault("GlobalRaidSettings.DisablePvPInRaids", false);
    }

    public static boolean clearMobsOnRaidLoss() {
        return Cfg.get().getOrDefault("GlobalRaidSettings.ClearMobsOnRaidLoss", false);
    }

    public static boolean arePlayerDeathMessagesShownInRaids() {
        return Cfg.get().getOrDefault("GlobalRaidSettings.ShowPlayerDeathMessagesInRaids", true);
    }

    public static boolean isMobSpawnsOutsideRaidAreaAllowed() {
        return Cfg.get().getOrDefault("GlobalSettings.AllowMobSpawnsOutsideRaidArea", false);
    }

    public static List<RaidPreset> getRaidPresets() {
        List<RaidPreset> raidPresets = new ArrayList<>();
        Map<?, ?> raidPresetsMap = Cfg.get().getMap("RaidPresets");
        for (Map.Entry<?, ?> presetEntry : raidPresetsMap.entrySet()) {
            final String presetName = presetEntry.getKey().toString();
            final String baseKey = "RaidPresets." + presetName + ".";
            final boolean isDefault = Cfg.get().getBoolean(baseKey + "default");
            final String boss = Cfg.get().getOrDefault(baseKey + "boss", "");
            final List<RaidMob> mobs = new ArrayList<>();
            Map<?, ?> mobMap = Cfg.get().getMap(baseKey + "mobs");
            for (Map.Entry<?, ?> mobEntry : mobMap.entrySet()) {
                final String mobName = mobEntry.getKey().toString();
                final String mobKey = baseKey + "mobs." + mobName + ".";
                final double level = Cfg.get().getDouble(mobKey + "mobLevel");
                final double chance = Cfg.get().getDouble(mobKey + "chance");
                final int weight = Cfg.get().getInt(mobKey + "weight");
                mobs.add(
                    new RaidMobBuilder()
                        .setName(mobName)
                        .setLevel(level)
                        .setChance(chance)
                        .setWeight(weight)
                        .build()
                );
            }
            raidPresets.add(
                new RaidPresetBuilder()
                    .setName(presetName)
                    .setDefault(isDefault)
                    .setBoss(boss)
                    .setRaidMobs(mobs)
                    .build()
            );
        }
        return raidPresets;
    }

    public static List<RaidTier> getRaidTiers() {
        List<RaidTier> raidTiers = new ArrayList<>();
        Map<?, ?> raidTiersMap = Cfg.get().getMap("RaidTiers");
        for (Map.Entry<?, ?> tierEntry : raidTiersMap.entrySet()) {
            final String name = tierEntry.getKey().toString();
            LinkedHashMap<?, ?> specificTierMap = (LinkedHashMap<?, ?>) tierEntry.getValue();
            final boolean isDefault = (boolean) specificTierMap.get("default");
            final int maxMobsAtOnce = (int) specificTierMap.get("maxMobsAtOnce");
            final int killsGoal = (int) specificTierMap.get("killsGoal");
            final int timeLimit = (int) specificTierMap.get("timeLimit");
            final int mobSpawnsPerCycle = (int) specificTierMap.get("mobSpawnsPerCycle");
            final double cycleRate = (double) specificTierMap.get("cycleRate");
            raidTiers.add(
                new RaidTierBuilder()
                    .setName(name)
                    .setDefault(isDefault)
                    .setMaxMobsAtOnce(maxMobsAtOnce)
                    .setKillsGoal(killsGoal)
                    .setTimeLimit(timeLimit)
                    .setMobSpawnsPerCycle(mobSpawnsPerCycle)
                    .setCycleRate(cycleRate)
                    .build()
            );
        }
        return raidTiers;
    }

    public static boolean isScoreboardEnabled() {
        return Cfg.get().getOrDefault("Scoreboard.enabled", false);
    }

    public static String getScoreboardTitle() {
        return Cfg.get().getString("Scoreboard.title");
    }

    public static String getScoreboardBorder() {
        return Cfg.get().getString("Scoreboard.border");
    }

    public static List<String> getScoreboardLines() {
        @SuppressWarnings("unchecked")
        List<String> scoreboardLines = (List<String>) Cfg.get().getList("Scoreboard.lines");
        return scoreboardLines;
    }

    public static boolean areTitleMessagesEnabled() {
        return Cfg.get().getOrDefault("TitleMessages.enabled", false);
    }

    public static String getRaidStartTitle() {
        return Cfg.get().getString("TitleMessages.raidStartTitle");
    }

    public static String getRaidStartSubtitle() {
        return Cfg.get().getString("TitleMessages.raidStartSubtitle");
    }

    public static String getRaidWinTitle() {
        return Cfg.get().getString("TitleMessages.raidWinTitle");
    }

    public static String getRaidWinSubtitle() {
        return Cfg.get().getString("TitleMessages.raidWinSubtitle");
    }

    public static String getRaidLoseTitle() {
        return Cfg.get().getString("TitleMessages.raidLoseTitle");
    }

    public static String getRaidLoseSubtitle() {
        return Cfg.get().getString("TitleMessages.raidLoseSubtitle");
    }

    public static String getRaidCancelTitle() {
        return Cfg.get().getString("TitleMessages.raidCancelTitle");
    }

    public static String getRaidCancelSubtitle() {
        return Cfg.get().getString("TitleMessages.raidCancelSubtitle");
    }

    public static String getRaidBossSpawnTitle() {
        return Cfg.get().getString("TitleMessages.raidBossSpawnTitle");
    }

    public static String getRaidBossSpawnSubtitle() {
        return Cfg.get().getString("TitleMessages.raidBossSpawnSubtitle");
    }

    public static boolean areRaidResultConsoleCommandsEnabled() {
        return Cfg.get().getOrDefault("RaidResultConsoleCommands.enabled", false);
    }

    public static List<String> getRaidWinGlobalCommands() {
        @SuppressWarnings("unchecked")
        List<String> globalWinCommands = (List<String>) Cfg.get().getList("RaidResultConsoleCommands.raidWinCommands.global");
        return globalWinCommands;
    }

    public static List<String> getRaidWinPerParticipantCommands() {
        @SuppressWarnings("unchecked")
        List<String> perParticipantWinCommands = (List<String>) Cfg.get().getList("RaidResultConsoleCommands.raidWinCommands.perParticipant");
        return perParticipantWinCommands;
    }

    public static List<String> getRaidLossGlobalCommands() {
        @SuppressWarnings("unchecked")
        List<String> globalLossCommands = (List<String>) Cfg.get().getList("RaidResultConsoleCommands.raidLossCommands.global");
        return globalLossCommands;
    }

    public static List<String> getRaidLossPerParticipantCommands() {
        @SuppressWarnings("unchecked")
        List<String> perParticipantsLossCommands = (List<String>) Cfg.get().getList("RaidResultConsoleCommands.raidLossCommands.perParticipant");
        return perParticipantsLossCommands;
    }

    public static boolean areRaidSchedulerAnnounceMessagedEnabled() {
        return Cfg.get().getOrDefault("RaidScheduler.AnnouncementMessages.enabled", false);
    }

    public static TreeMap<Integer, String> getRaidSchedulerAnnouncements() {
        TreeMap<Integer, String> announcementMessages = new TreeMap<>(Comparator.reverseOrder());
        Map<?, ?> announcementMessagesMap = Cfg.get().getMap("RaidScheduler.AnnouncementMessages");
        @SuppressWarnings("unchecked")
        List<Map<?, ?>> intervalsList = (List<Map<?, ?>>) announcementMessagesMap.get("CustomIntervals");
        for (Map<?, ?> intervalEntry : intervalsList) {
            final int interval = (int) intervalEntry.get("interval");
            final String message = (String) intervalEntry.get("message");
            announcementMessages.put(interval, message);
        }
        return announcementMessages;
    }

}
