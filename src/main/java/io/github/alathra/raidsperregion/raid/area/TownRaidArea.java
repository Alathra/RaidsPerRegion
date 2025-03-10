package io.github.alathra.raidsperregion.raid.area;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.town.toggle.TownToggleMobsEvent;
import com.palmergames.bukkit.towny.exceptions.CancelledEventException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.palmergames.bukkit.util.BukkitTools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

public class TownRaidArea extends RaidArea {

    private final Town town;

    public TownRaidArea(Town base) {
        super(base);
        town = base;
        setName();
        setType();
    }

    public static Set<String> getAllTownNames(@NotNull World world) {
        TownyAPI townyAPI = TownyAPI.getInstance();
        TownyWorld townyWorld = townyAPI.getTownyWorld(world);
        if (townyWorld != null)
            return townyWorld.getTowns().keySet();
        return Collections.emptySet();
    }

    @Override
    protected void setName() {
        super.name = town.getName();
    }

    @Override
    protected void setType() {
        super.type = "town";
    }

    @Override
    public boolean forceMobSpawning() {
        if (!town.hasMobs()) {
            try {
                // Fire cancellable event directly before setting the toggle.
                TownToggleMobsEvent preEvent = new TownToggleMobsEvent(Bukkit.getConsoleSender(), town, true, true);
                BukkitTools.ifCancelledThenThrow(preEvent);
                // Set the toggle setting.
                town.setHasMobs(preEvent.getFutureState());
                // Propagate perms to all unchanged, town owned, townblocks because it is a
                // townblock-affecting toggle.
                for (TownBlock townBlock : town.getTownBlocks()) {
                    if (!townBlock.hasResident() && !townBlock.isChanged()) {
                        townBlock.setType(townBlock.getType());
                        townBlock.save();
                    }
                }
                super.wasMobSpawningEnabledBeforeRaid = false;
                return true;
            } catch (CancelledEventException e) {
                return false;
            }
        } else {
            super.wasMobSpawningEnabledBeforeRaid = true;
        }
        return true;
    }

    @Override
    public boolean resetMobSpawningToDefault() {
        if (!super.wasMobSpawningEnabledBeforeRaid) {
            try {
                // Fire cancellable event directly before setting the toggle.
                TownToggleMobsEvent preEvent = new TownToggleMobsEvent(Bukkit.getConsoleSender(), town, true, false);
                BukkitTools.ifCancelledThenThrow(preEvent);
                // Set the toggle setting.
                town.setHasMobs(preEvent.getFutureState());
                // Propagate perms to all unchanged, town owned, townblocks because it is a
                // townblock-affecting toggle.
                for (TownBlock townBlock : town.getTownBlocks()) {
                    if (!townBlock.hasResident() && !townBlock.isChanged()) {
                        townBlock.setType(townBlock.getType());
                        townBlock.save();
                    }
                }
                return true;
            } catch (CancelledEventException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsLocation(Location location) {
        Town townAtLocation = WorldCoord.parseWorldCoord(location).getTownOrNull();
        if (townAtLocation == null) {
            return false;
        }
        return townAtLocation.equals(town);
    }
}
