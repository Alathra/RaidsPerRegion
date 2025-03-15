package io.github.alathra.raidsperregion.raid.area;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FactionRaidArea extends RaidArea {

    private final Faction faction;

    public FactionRaidArea(Faction faction) {
        super(faction);
        this.faction = faction;
        setName();
        setType();
    }

    public static Set<String> getAllFactionNames(@NotNull World world) {
        // Get all factions
        List<Faction> factions = new ArrayList<>(Factions.getInstance().getAllFactions());
        // Prune all not in selected world
        factions.removeIf(faction -> !faction.hasHome() || !faction.getHome().getWorld().equals(world));
        return factions.stream().map(Faction::getTag).collect(Collectors.toSet());
    }


    @Override
    protected void setName() {
        super.name = faction.getTag();
    }

    @Override
    protected void setType() {
        super.type = "faction";
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
        FLocation fLocation = new FLocation(location);
        Faction factionAtLocation = Board.getInstance().getFactionAt(fLocation);
        if (factionAtLocation == null || factionAtLocation.isWilderness() || factionAtLocation.isSafeZone() || factionAtLocation.isWarZone()) {
            return false;
        }
        return factionAtLocation.getIntId() == faction.getIntId();
    }
}
