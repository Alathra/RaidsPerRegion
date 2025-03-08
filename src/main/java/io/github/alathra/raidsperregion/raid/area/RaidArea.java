package io.github.alathra.raidsperregion.raid.area;

import io.github.alathra.raidsperregion.raid.Raid;
import org.bukkit.Location;

import java.util.Set;
import java.util.UUID;

public abstract class RaidArea {
    protected String name;
    protected String type;
    protected final Object base;

    protected boolean wasMobSpawningEnabledBeforeRaid;

    public RaidArea(Object base) {
        this.base = base;
    }

    protected abstract void setName();
    protected abstract void setType();

    public abstract Set<UUID> findPlayersInArea();
    public abstract void forceMobSpawning();
    public abstract void resetMobSpawningToDefault();
    public abstract boolean containsLocation(Location location);


    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Object getBase() {
        return base;
    }

}
