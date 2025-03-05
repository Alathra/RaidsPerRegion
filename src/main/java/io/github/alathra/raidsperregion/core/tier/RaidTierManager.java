package io.github.alathra.raidsperregion.core.tier;

import io.github.alathra.raidsperregion.config.Settings;
import io.github.alathra.raidsperregion.utility.Logger;

import java.util.ArrayList;
import java.util.List;

public class RaidTierManager {

    public static List<RaidTier> raidTiers = new ArrayList<>();

    public static void refreshPresets() {
        raidTiers.clear();
        Settings.getRaidTiers().forEach(RaidTierManager::registerTier);
    }

    public static void registerTier(RaidTier raidTier) {
        deregisterTier(raidTier);
        if (raidTier.isDefault() && getDefinedDefaultOrNull() != null) {
            raidTier = new RaidTierBuilder()
                .setName(raidTier.getName())
                .setDefault(false)
                .setMaxMobsAtOnce(raidTier.getMaxMobsAtOnce())
                .setKillsGoal(raidTier.getKillsGoal())
                .setTimeLimit(raidTier.getTimeLimit())
                .setMobSpawnsPerCycle(raidTier.getMobSpawnsPerCycle())
                .setCycleRate(raidTier.getCycleRate())
                .build();
            Logger.get().warn("Tier Register Warning: Multiple tiers set as default. This may produce unexpected behavior.");
        }
        raidTiers.add(raidTier);
    }

    public static void deregisterTier(RaidTier raidTier) {
        RaidTier matching = findMatchingNameOrNull(raidTier);
        if (matching != null) {
            raidTiers.remove(matching);
        }
    }

    public static RaidTier findMatchingNameOrNull(RaidTier newRaidTier) {
        for (RaidTier raidTier : raidTiers) {
            if (newRaidTier.getName().equalsIgnoreCase(raidTier.getName())) {
                return raidTier;
            }
        }
        return null;
    }

    public static RaidTier getDefinedDefaultOrNull() {
        for (RaidTier raidTier : raidTiers) {
            if (raidTier.isDefault()) {
                return raidTier;
            }
        }
        return null;
    }
}
