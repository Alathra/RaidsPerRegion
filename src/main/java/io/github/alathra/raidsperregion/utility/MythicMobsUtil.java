package io.github.alathra.raidsperregion.utility;

import io.github.alathra.raidsperregion.raid.Raid;
import io.github.alathra.raidsperregion.raid.mob.RaidMob;
import io.github.alathra.raidsperregion.raid.preset.RaidPreset;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.bukkit.Location;

public class MythicMobsUtil {

    public static MobExecutor mobManager;

    public static void init() {
        mobManager = MythicBukkit.inst().getMobManager();
    }

    public static ActiveMob spawnMob(String mobName, Location loc) {
        return mobManager.spawnMob(mobName, loc);
    }

    public static ActiveMob spawnMob(String mobName, Location loc, double level) {
        return mobManager.spawnMob(mobName, loc, level);
    }

    public static ActiveMob spawnRandomMob(RaidPreset preset, Location loc) {
        // random number between 0 and 1
        double random = Math.random();

        int topPriority = 1;
        String mobName = preset.getMobs().getFirst().getName();
        double mobLevel = preset.getMobs().getFirst().getLevel();
        for (RaidMob raidMob : preset.getMobs()) {
            if (random < raidMob.getChance()) {
                if (raidMob.getWeight() >= topPriority) {
                    mobName = raidMob.getName();
                    mobLevel = raidMob.getLevel();
                }
            }
        }
        return spawnMob(mobName, loc, mobLevel);
    }

    public static MobExecutor getMobManager() {
        return mobManager;
    }
}
