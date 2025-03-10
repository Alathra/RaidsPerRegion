package io.github.alathra.raidsperregion.raid.area;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.alathra.raidsperregion.utility.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

public class RegionRaidArea extends RaidArea {

    private final ProtectedRegion region;

    public RegionRaidArea(ProtectedRegion base) {
        super(base);
        region = base;
        setName();
        setType();
    }

    public static Set<String> getAllRegionNames(@NotNull World world) {
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(world));
        if (regionManager == null) {
            Logger.get().warn("WorldGuard RegionManager could not be loaded for world '{}'", world.getName());
            return Collections.emptySet();
        }
        return regionManager.getRegions().keySet();
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
    public boolean forceMobSpawning() {
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
        return true;
    }

    @Override
    public boolean resetMobSpawningToDefault() {
        if (!super.wasMobSpawningEnabledBeforeRaid) {
            region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
        }
        return true;
    }

    @Override
    public boolean containsLocation(Location location) {
        return region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    private ProtectedRegion getRegion() {
        return region;
    }

}
