package io.github.alathra.raidsperregion.raid.area;

import io.github.alathra.raidsperregion.RaidsPerRegion;
import io.github.alathra.raidsperregion.hook.Hook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class RaidArea {

    public static Set<String> types = new HashSet<>();

    protected String name;
    protected String type;
    protected final Object base;

    protected boolean wasMobSpawningEnabledBeforeRaid;

    public RaidArea(Object base) {
        this.base = base;
    }

    protected abstract void setName();
    protected abstract void setType();

    public abstract boolean forceMobSpawning();
    public abstract boolean resetMobSpawningToDefault();
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

    public static void setTypes() {
        types.add("region");
        Bukkit.getScheduler().runTaskLater(RaidsPerRegion.getInstance(), () -> {
            if (Hook.Towny.isLoaded())
                types.add("town");
            if (Hook.KingdomsX.isLoaded())
                types.add("kingdom");
            if (Hook.FactionsUUID.isLoaded())
                types.add("faction");
        }, 1L);
    }

    public static Set<String> getTypes() {
        return types;
    }

}
