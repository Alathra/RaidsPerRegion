package io.github.alathra.raidsperregion.listener;

import io.github.alathra.raidsperregion.raid.Raid;
import io.github.alathra.raidsperregion.raid.RaidManager;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RaidKillListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMythicMobDeath(MythicMobDeathEvent e) {
        for (Raid raid : RaidManager.getRaids()) {

            // Check for boss kill
            if (raid.getPreset().hasBoss()) {
                if (raid.hasBossSpawned() && !raid.hasBossBeenKilled()) {
                    if (raid.getBossMob() != null) {
                        if (e.getMob().getUniqueId().equals(raid.getBossMob().getUniqueId())) {
                            raid.setBossKilled(true);
                            return;
                        }
                    }
                }
            }

            // Move on to next raid if the mob is not part of this raid
            Set<UUID> mobUUIDs = raid.getMobs().stream().map(ActiveMob::getUniqueId).collect(Collectors.toSet());
            for (UUID mobUUID : mobUUIDs) {
                if (e.getMob().getUniqueId().equals(mobUUID)) {
                    if (e.getKiller() instanceof Player player) {
                        // Somehow an unregistered player killed the mob, ignore
                        if (!raid.getParticipantsTable().containsRow(player.getUniqueId())) {
                            return;
                        }
                        // Increase player's kills, update table
                        Integer playersKills = raid.getKills(player.getUniqueId());
                        if (playersKills == null) {
                            return;
                        }
                        Sidebar sidebar = raid.getParticipantsTable().get(player.getUniqueId(), playersKills);
                        raid.getParticipantsTable().remove(player.getUniqueId(), playersKills);
                        playersKills++;
                        raid.getParticipantsTable().put(player.getUniqueId(), playersKills, sidebar);
                    }
                }
            }
        }

    }
}