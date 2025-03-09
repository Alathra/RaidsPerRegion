package io.github.alathra.raidsperregion.raid.area;

import org.bukkit.Location;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.land.Land;

public class KingdomRaidArea extends RaidArea {

    private final Kingdom kingdom;

    public KingdomRaidArea(Kingdom kingdom) {
        super(kingdom);
        this.kingdom = kingdom;
        setName();
        setType();
    }

    @Override
    protected void setName() {
        super.name = kingdom.getName();
    }

    @Override
    protected void setType() {
        super.type = "kingdom";
    }

    @Override
    public boolean forceMobSpawning() {
        // TODO:
        return true;
    }

    @Override
    public boolean resetMobSpawningToDefault() {
        // TODO:
        return true;
    }

    @Override
    public boolean containsLocation(Location location) {
        Land land = Land.getLand(location);
        if (land != null) {
            if (land.isClaimed()) {
                Kingdom landKingdom = land.getKingdom();
                if (landKingdom != null) {
                    // if kingdom is the raid area
                    if (landKingdom.getId().equals(kingdom.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
