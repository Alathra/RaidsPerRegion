package io.github.alathra.raidsperregion.raid.area;

import io.github.alathra.raidsperregion.hook.Hook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
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

    public boolean wasMobSpawningEnabledBeforeRaid() {
        return wasMobSpawningEnabledBeforeRaid;
    }

    public Set<UUID> findPlayersInArea() {
        Set<UUID> playerUUIDs = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (containsLocation(player.getLocation())) {
                playerUUIDs.add(player.getUniqueId());
            }
        }
        return playerUUIDs;
    }

    public static Set<String> getTypes() {
        Set<String> raidTypes = new HashSet<>();
        raidTypes.add("region");
        if(Hook.Towny.isLoaded()) {
            raidTypes.add("town");
        }
        return raidTypes;
    }

}
