package io.github.alathra.raidsperregion.command;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.jorel.commandapi.arguments.*;
import io.github.alathra.raidsperregion.hook.Hook;
import io.github.alathra.raidsperregion.raid.Raid;
import io.github.alathra.raidsperregion.raid.RaidManager;
import io.github.alathra.raidsperregion.raid.area.KingdomRaidArea;
import io.github.alathra.raidsperregion.raid.area.RaidArea;
import io.github.alathra.raidsperregion.raid.area.RegionRaidArea;
import io.github.alathra.raidsperregion.raid.area.TownRaidArea;
import io.github.alathra.raidsperregion.raid.preset.RaidPreset;
import io.github.alathra.raidsperregion.raid.preset.RaidPresetManager;
import io.github.alathra.raidsperregion.raid.tier.RaidTier;
import io.github.alathra.raidsperregion.raid.tier.RaidTierManager;
import io.github.alathra.raidsperregion.utility.RandomUtil;
import io.github.milkdrinkers.colorparser.ColorParser;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.kingdoms.constants.group.Kingdom;

import java.util.*;
import java.util.stream.Collectors;

public class CommandUtil {

    public static Argument<RaidArea> createRaidAreaArgument(String nodeName, String typeNodeName, String worldNodeName) {
        return new CustomArgument<RaidArea, String>(new StringArgument(nodeName), info -> {
            String argAreaName = info.input().toLowerCase();
            final String raidType = (String) info.previousArgs().get(typeNodeName);
            if (raidType == null)
                throw CustomArgument.CustomArgumentException.fromAdventureComponent(ColorParser.of("<red>Invalid raid type argument").build());
            if (!RaidArea.getTypes().contains(raidType))
                throw CustomArgument.CustomArgumentException.fromAdventureComponent(ColorParser.of("<red>Invalid raid type argument").build());
            if (!(info.previousArgs().get(worldNodeName) instanceof World world))
                throw CustomArgument.CustomArgumentException.fromAdventureComponent(ColorParser.of("<red>Invalid world argument").build());

            switch (raidType) {
                case "region":
                    RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(world));
                    if (regionManager == null)
                        throw CustomArgument.CustomArgumentException.fromAdventureComponent(ColorParser.of("<red>Encountered error when loading Worldguard RegionManager").build());
                    if (argAreaName.equalsIgnoreCase("random")) {
                        argAreaName = RandomUtil.getRandomElementInSet(RegionRaidArea.getAllRegionNames(world));
                        if (argAreaName == null)
                            throw CustomArgument.CustomArgumentException.fromAdventureComponent(ColorParser.of("<red>Failed to get random region").build());
                    }
                    ProtectedRegion region = regionManager.getRegion(argAreaName);
                    if (region == null)
                        throw CustomArgument.CustomArgumentException.fromAdventureComponent(ColorParser.of("<red>Invalid region name").build());
                    return new RegionRaidArea(region);
                case "town":
                    if (Hook.getTownyHook().isHookLoaded()) {
                        if (argAreaName.equalsIgnoreCase("random")) {
                            argAreaName = RandomUtil.getRandomElementInSet(TownRaidArea.getAllTownNames(world));
                            if (argAreaName == null)
                                throw CustomArgument.CustomArgumentException.fromAdventureComponent(ColorParser.of("<red>Failed to get random town").build());
                        }
                        TownyAPI townyAPI = TownyAPI.getInstance();
                        Town town = townyAPI.getTown(argAreaName);
                        if (town == null)
                            throw CustomArgument.CustomArgumentException.fromAdventureComponent(ColorParser.of("<red>Invalid town name").build());
                        return new TownRaidArea(town);
                    }
                case "kingdom":
                    if (Hook.KingdomsX.isLoaded()) {
                        if (argAreaName.equalsIgnoreCase("random")) {
                            argAreaName = RandomUtil.getRandomElementInSet(KingdomRaidArea.getAllKingdomNames(world));
                            if (argAreaName == null)
                                throw CustomArgument.CustomArgumentException.fromAdventureComponent(ColorParser.of("<red>Failed to get random kingdom").build());
                        }
                        Kingdom kingdom = Kingdom.getKingdom(argAreaName);
                        if (kingdom == null)
                            throw CustomArgument.CustomArgumentException.fromAdventureComponent(ColorParser.of("<red>Invalid kingdom name").build());
                        return new KingdomRaidArea(kingdom);
                    }
                default:
                    throw CustomArgument.CustomArgumentException.fromAdventureComponent(ColorParser.of("<red>Invalid raid type argument").build());
            }
        }).replaceSuggestions(ArgumentSuggestions.stringCollection(info -> {
            final String raidType = (String) info.previousArgs().get(typeNodeName);
            if (raidType == null)
                return Collections.emptyList();
            if (!RaidArea.getTypes().contains(raidType))
                return Collections.emptyList();
            if (!(info.previousArgs().get(worldNodeName) instanceof World world))
                return Collections.emptyList();

            switch (raidType) {
                case "region":
                    Set<String> regionNames = new HashSet<>(RegionRaidArea.getAllRegionNames(world));
                    regionNames.add("random");
                    return regionNames;
                case "town":
                    if (Hook.Towny.isLoaded()) {
                        Set<String> townNames = new HashSet<>(TownRaidArea.getAllTownNames(world));
                        townNames.add("random");
                        return townNames;
                    }
                    return Collections.emptyList();
                case "kingdom":
                    if (Hook.KingdomsX.isLoaded()) {
                        Set<String> kingdomNames = new HashSet<>(KingdomRaidArea.getAllKingdomNames(world));
                        kingdomNames.add("random");
                        return kingdomNames;
                    }
                    return Collections.emptyList();
                default:
                    return Collections.emptyList();
            }
        }));
    }

    public static Argument<RaidPreset> raidPresetArgument(String nodeName) {
        return new CustomArgument<RaidPreset, String>(new StringArgument(nodeName), info -> {
            final String argPresetName = info.input().toLowerCase();
            RaidPresetManager.refreshPresets();
            for (RaidPreset raidPreset : RaidPresetManager.getPresets()) {
                if (argPresetName.equalsIgnoreCase(raidPreset.getName())) {
                    return raidPreset;
                }
            }
            throw CustomArgument.CustomArgumentException.fromAdventureComponent(ColorParser.of("<red>Invalid raid preset argument").build());
        }).replaceSuggestions(ArgumentSuggestions.stringCollection(info -> {
            RaidPresetManager.refreshPresets();
            return RaidPresetManager.getPresets().stream().map(RaidPreset::getName).toList();
        }));
    }

    public static Argument<RaidTier> raidTierArgument(String nodeName) {
        return new CustomArgument<RaidTier, String>(new StringArgument(nodeName), info -> {
            final String argTierName = info.input().toLowerCase();
            RaidTierManager.refreshTiers();
            for (RaidTier raidTier : RaidTierManager.getTiers()) {
                if (argTierName.equalsIgnoreCase(raidTier.getName())) {
                    return raidTier;
                }
            }
            throw CustomArgument.CustomArgumentException.fromAdventureComponent(ColorParser.of("<red>Invalid raid tier argument").build());
        }).replaceSuggestions(ArgumentSuggestions.stringCollection(info -> {
            RaidTierManager.refreshTiers();
            return RaidTierManager.getTiers().stream().map(RaidTier::getName).toList();
        }));
    }

    public static Argument<Raid> raidArgument(String nodeName) {
        return new CustomArgument<Raid, String>(new StringArgument(nodeName), info -> {
            final String argRaidName = info.input().toLowerCase();
            for (Raid raid : RaidManager.getRaids()) {
                if (argRaidName.equalsIgnoreCase(raid.getArea().getName())) {
                    return raid;
                }
            }
            throw CustomArgument.CustomArgumentException.fromAdventureComponent(ColorParser.of("<red>Invalid raid argument").build());
        }).replaceSuggestions(ArgumentSuggestions.stringCollection(info -> RaidManager.getRaids().stream().map(raid -> raid.getArea().getName()).collect(Collectors.toList())));
    }

    public static Argument<World> worldArgument(String nodeName) {
        return new CustomArgument<World, String>(new StringArgument(nodeName), info -> {
            final String argWorldName = info.input();
            for (World world : Bukkit.getWorlds()) {
                if (argWorldName.equalsIgnoreCase(world.getName())) {
                    return world;
                }
            }
            throw CustomArgument.CustomArgumentException.fromAdventureComponent(ColorParser.of("<red>Invalid world argument").build());
        }).replaceSuggestions(ArgumentSuggestions.strings(Bukkit.getWorlds().stream().map(World::getName).toList()));
    }

    public static Argument<String> raidTypeArgument(String nodeName) {
        return new CustomArgument<String, String>(new StringArgument(nodeName), info -> {
            final String argTypeName = info.input().toLowerCase();
            if (!RaidArea.getTypes().contains(argTypeName)) {
                throw CustomArgument.CustomArgumentException.fromAdventureComponent(ColorParser.of("<red>Invalid raid type argument").build());
            }
            return argTypeName;
        }).replaceSuggestions(ArgumentSuggestions.strings(RaidArea.getTypes()));
    }

    public static Argument<Integer> raidScheduledArgument(String nodeName) {
        return new CustomArgument<Integer, String>(new StringArgument(nodeName), info -> {
            final String argScheduledMinutes = info.input();
            int minutes;
            try {
                minutes = Integer.parseInt(argScheduledMinutes);
            } catch (NumberFormatException e) {
                throw CustomArgument.CustomArgumentException.fromAdventureComponent(ColorParser.of("<red>Invalid schedule argument").build());
            }
            if (minutes <= 0) {
                throw CustomArgument.CustomArgumentException.fromAdventureComponent(ColorParser.of("<red>Invalid schedule argument").build());
            }
            return minutes;
        }).replaceSuggestions(ArgumentSuggestions.strings("30", "20", "15", "10", "5"));
    }

}
