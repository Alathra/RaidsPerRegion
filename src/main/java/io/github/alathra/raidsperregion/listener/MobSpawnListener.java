package io.github.alathra.raidsperregion.listener;

import io.github.alathra.raidsperregion.RaidsPerRegion;
import io.github.alathra.raidsperregion.config.Settings;
import io.github.alathra.raidsperregion.raid.Raid;
import io.github.alathra.raidsperregion.raid.RaidManager;
import io.github.alathra.raidsperregion.utility.MythicMobsUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MobSpawnListener implements Listener {

    private final RaidsPerRegion plugin;

    public MobSpawnListener(RaidsPerRegion plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (!Settings.preventVanillaMobsSpawnInRaids()) {
            return;
        }

        if (RaidManager.getRaids().isEmpty()) {
            return;
        }

        // clear any non-raid mobs that spawn
        Entity entity = e.getEntity();
        Location spawnLocation = e.getLocation();

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            // for each raid, stop vanilla mobs from spawning in raid area
            if(!MythicMobsUtil.getMobManager().isMythicMob(entity)) {
                for (Raid raid : RaidManager.getRaids()) {
                    if (raid.getArea().containsLocation(spawnLocation)) {
                        e.getEntity().remove();
                        return;
                    }
                }
            }
        }, 1L);
    }
}
