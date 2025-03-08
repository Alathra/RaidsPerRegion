package io.github.alathra.raidsperregion.raid.area;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RegionRaidArea extends RaidArea {

    private final ProtectedRegion region;

    public RegionRaidArea(ProtectedRegion base) {
        super(base);
        region = base;
    }

    @Override
    protected void setName() {
        super.name = region.getId();
    }

    @Override
    protected void setType() {
        super.type = "region";
    }

    @Override
    public void forceMobSpawning() {
        if (region.getFlag(Flags.MOB_SPAWNING) != null) {
            if (region.getFlag(Flags.MOB_SPAWNING).equals(StateFlag.State.ALLOW)) {
                super.wasMobSpawningEnabledBeforeRaid = true;
            } else {
                // StateFlag.State.DENY, so force spawning
                region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.ALLOW);
                super.wasMobSpawningEnabledBeforeRaid = false;
            }
        } else {
            // No flag will default to true
            super.wasMobSpawningEnabledBeforeRaid = true;
            region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.ALLOW);
        }
    }

    @Override
    public void resetMobSpawningToDefault() {
        if (!super.wasMobSpawningEnabledBeforeRaid) {
            region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
        }
    }

    @Override
    public boolean containsLocation(Location location) {
        return region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public Set<UUID> findPlayersInArea() {
        Set<UUID> playerUUIDs = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            Location location = player.getLocation();

            if (region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
                playerUUIDs.add(player.getUniqueId());
            }
        }
        return playerUUIDs;
    }

    private ProtectedRegion getRegion() {
        return region;
    }
}
