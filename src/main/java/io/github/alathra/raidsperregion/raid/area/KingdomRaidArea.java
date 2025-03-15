package io.github.alathra.raidsperregion.raid.area;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.land.Land;
import org.kingdoms.main.Kingdoms;

import java.util.*;
import java.util.stream.Collectors;

public class KingdomRaidArea extends RaidArea {

    private final Kingdom kingdom;

    public KingdomRaidArea(Kingdom kingdom) {
        super(kingdom);
        this.kingdom = kingdom;
        setName();
        setType();
    }

    public static Set<String> getAllKingdomNames(@NotNull World world) {
        // Get all kingdoms
        List<Kingdom> kingdoms = new ArrayList<>(Kingdoms.get().getDataCenter().getKingdomManager().getKingdoms());
        // Prune all not in selected world
        kingdoms.removeIf(kingdom -> !kingdom.getNexus().getBukkitWorld().equals(world));
        return kingdoms.stream().map(Kingdom::getName).collect(Collectors.toSet());
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
        return true;
    }

    @Override
    public boolean resetMobSpawningToDefault() {
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
