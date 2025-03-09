package io.github.alathra.raidsperregion.listener;

import io.github.alathra.raidsperregion.config.Settings;
import io.github.alathra.raidsperregion.raid.Raid;
import io.github.alathra.raidsperregion.raid.RaidManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getPlayer();

        for (Raid raid : RaidManager.getRaids()) {
            if (raid.getActiveParticipants().contains(player.getUniqueId())) {
                if (Settings.keepInventoryOnPlayerDeath())
                    e.setKeepInventory(true);
                if (Settings.keepEXPOnPlayerDeath())
                    e.setKeepLevel(true);
                if (!Settings.arePlayerDeathMessagesShownInRaids())
                    e.deathMessage(null);
                raid.addPlayerDeath();
            }
        }
    }
}
