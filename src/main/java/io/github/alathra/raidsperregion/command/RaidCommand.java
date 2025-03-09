package io.github.alathra.raidsperregion.command;

import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.WorldArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.alathra.raidsperregion.RaidsPerRegion;
import io.github.alathra.raidsperregion.raid.Raid;
import io.github.alathra.raidsperregion.raid.RaidManager;
import io.github.alathra.raidsperregion.raid.area.RaidArea;
import io.github.alathra.raidsperregion.raid.preset.RaidPreset;
import io.github.alathra.raidsperregion.raid.preset.RaidPresetManager;
import io.github.alathra.raidsperregion.raid.tier.RaidTier;
import io.github.alathra.raidsperregion.raid.tier.RaidTierManager;
import io.github.milkdrinkers.colorparser.ColorParser;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class RaidCommand {

    protected RaidCommand() {
        new CommandAPICommand("raid")
            .withFullDescription("RaidsPerRegion raid commands")
            .withShortDescription("RaidsPerRegion raid commands")
            .withSubcommands(
                startCommand(),
                stopCommand(),
                listCommand()
            )
            .executes(this::helpCommand)
            .register();
    }

    private void helpMsg(CommandSender sender) {
        sender.sendMessage("[-----RaidsPerRegion4 Help Menu----]");
        sender.sendMessage(ColorParser.of("Start a raid: <yellow>/raid start [type] [world] [area] <preset> <tier>").build());
        sender.sendMessage(ColorParser.of("Cancel a raid: <yellow>/raid stop [raid]").build());
        sender.sendMessage(ColorParser.of("View active raids: <yellow>/raid list").build());
    }

    private void helpCommand(CommandSender sender, CommandArguments args) {
        helpMsg(sender);
    }

    public CommandAPICommand startCommand() {
        return new CommandAPICommand("start")
            .withPermission(RaidsPerRegion.getAdminPermission())
            .withArguments(
                new StringArgument("type").replaceSuggestions(ArgumentSuggestions.strings(RaidArea.getTypes())),
                new WorldArgument("world").replaceSuggestions(ArgumentSuggestions.strings(Bukkit.getWorlds().stream().map(World::getName).toList())),
                CommandUtil.createRaidAreaArgument("area", "type", "world")
            )
            .withOptionalArguments(
                CommandUtil.raidPresetArgument("preset"),
                CommandUtil.raidTierArgument("tier")
            )
            .executes((CommandSender sender, CommandArguments args) -> {
                final String argType = (String) args.get("type");
                if (argType == null)
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>Invalid raid type argument").build());
                if (!RaidArea.getTypes().contains(argType))
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>Invalid raid type argument").build());

                if (!(args.get("world") instanceof World world))
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>Invalid world argument").build());

                final RaidArea raidArea = (RaidArea) args.get("area");

                RaidPreset raidPreset = RaidPresetManager.getDefinedDefaultOrNull();
                if (raidPreset == null) {
                    RaidPresetManager.getPresets().getFirst();
                }
                if (args.getOptional("preset").isPresent()) {
                    raidPreset = (RaidPreset) args.get("preset");
                }

                RaidTier raidTier = RaidTierManager.getDefinedDefaultOrNull();
                if (raidTier == null) {
                    RaidTierManager.getTiers().getFirst();
                }
                if (args.getOptional("tier").isPresent()) {
                    raidTier = (RaidTier) args.get("tier");
                }

                if (raidArea == null)
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>Invalid raid area argument").build());
                if (raidPreset == null)
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>Invalid raid preset argument").build());
                if (raidTier == null)
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>Invalid raid tier argument").build());

                Raid raid = new Raid(sender, world, raidArea, raidPreset, raidTier);
                if (RaidManager.registerRaid(raid)) {
                    raid.start();
                    sender.sendMessage(raid.parseMessage("<green>You have started a tier <raid_tier> raid on <raid_area_name>"));
                }
            });
    }

    public CommandAPICommand stopCommand() {
        return new CommandAPICommand("stop")
            .withPermission(RaidsPerRegion.getAdminPermission())
            .withArguments(
                CommandUtil.raidArgument("raid")
            )
            .executes((CommandSender sender, CommandArguments args) -> {
                final Raid raid = (Raid) args.get("area");
                if (raid == null)
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>Invalid raid argument").build());

                sender.sendMessage(raid.parseMessage("<green>You have stopped a tier <raid_tier> raid on <raid_area_name>"));
                raid.cancel();
            });
    }

    public CommandAPICommand listCommand() {
        return new CommandAPICommand("list")
            .withPermission(RaidsPerRegion.getAdminPermission())
            .executes((CommandSender sender, CommandArguments args) -> {
                String raidsString = Strings.join(RaidManager.getRaids().stream().map(raid -> raid.getArea().getName()).toList(), ',');
                sender.sendMessage(ColorParser.of("<yellow>Active Raids: " + raidsString).build());
            });
    }

}
