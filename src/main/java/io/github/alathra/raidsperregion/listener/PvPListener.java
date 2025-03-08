package io.github.alathra.raidsperregion.listener;

import io.github.alathra.raidsperregion.config.Settings;
import io.github.alathra.raidsperregion.raid.Raid;
import io.github.alathra.raidsperregion.raid.RaidManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PvPListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!Settings.disablePvPInRaids()) {
            return;
        }

        if (e.getEntity() instanceof Player damaged && e.getDamager() instanceof Player) {
            for (Raid raid : RaidManager.getRaids()) {
                if (raid.getActiveParticipants().contains(damaged.getUniqueId())) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

}
