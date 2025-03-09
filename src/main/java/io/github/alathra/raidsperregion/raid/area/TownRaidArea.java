package io.github.alathra.raidsperregion.raid.area;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.WorldCoord;
import org.bukkit.Location;

public class TownRaidArea extends RaidArea {

    private final Town town;

    public TownRaidArea(Town base) {
        super(base);
        town = base;
        setName();
        setType();
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
    public void forceMobSpawning() {
        if (!town.hasMobs()) {
            town.setHasMobs(true);
            town.saveTownBlocks();
            town.save();
            super.wasMobSpawningEnabledBeforeRaid = false;
        } else {
            super.wasMobSpawningEnabledBeforeRaid = true;
        }
    }

    @Override
    public void resetMobSpawningToDefault() {
        if (!super.wasMobSpawningEnabledBeforeRaid) {
            town.setHasMobs(false);
            town.saveTownBlocks();
            town.save();
        }
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
