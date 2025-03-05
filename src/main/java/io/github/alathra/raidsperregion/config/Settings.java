package io.github.alathra.raidsperregion.config;

import io.github.alathra.raidsperregion.core.mob.RaidMob;
import io.github.alathra.raidsperregion.core.mob.RaidMobBuilder;
import io.github.alathra.raidsperregion.core.preset.RaidPreset;
import io.github.alathra.raidsperregion.core.preset.RaidPresetBuilder;
import io.github.alathra.raidsperregion.core.tier.RaidTier;
import io.github.alathra.raidsperregion.core.tier.RaidTierBuilder;
import io.github.alathra.raidsperregion.utility.Cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Settings {

    public static boolean preventVanillaMobsSpawnInRaids() {
        return Cfg.get().get("GlobalRaidSettings.PreventVanillaMobsSpawningInRaids", true);
    }

    public static boolean forceMobSpawningInRegionOnRaidStart() {
        return Cfg.get().get("GlobalSettings.ForceMobSpawningInRegionOnRaidStart", true);
    }

    public static boolean keepInventoryOnPlayerDeath() {
        return Cfg.get().get("GlobalSettings.KeepInventoryOnPlayerDeath", false);
    }

    public static boolean keepEXPOnPlayerDeath() {
        return Cfg.get().get("GlobalSettings.KeepEXPOnPlayerDeath", false);
    }

    public static boolean disablePvPInRaids() {
        return Cfg.get().get("GlobalSettings.DisablePvPInRaids", false);
    }

    public static boolean clearMobsOnRaidLoss() {
        return Cfg.get().get("GlobalSettings.ClearMobsOnRaidLoss", false);
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
            final String baseKey = "RaidTiers." + name + ".";
            final boolean isDefault = Cfg.get().getBoolean(baseKey + "default");
            final int maxMobsAtOnce = Cfg.get().getInt(baseKey + "maxMobsAtOnce");
            final int killsGoal = Cfg.get().getInt(baseKey + "killsGoal");
            final int timeLimit = Cfg.get().getInt(baseKey + "timeLimit");
            final int mobSpawnsPerCycle = Cfg.get().getInt(baseKey + "mobSpawnsPerCycle");
            final double cycleRate = Cfg.get().getDouble(baseKey + "cycleRate");
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


}
