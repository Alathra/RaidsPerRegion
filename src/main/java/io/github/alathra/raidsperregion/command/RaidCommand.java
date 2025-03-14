package io.github.alathra.raidsperregion.command;

import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.alathra.raidsperregion.RaidsPerRegion;
import io.github.alathra.raidsperregion.raid.Raid;
import io.github.alathra.raidsperregion.raid.RaidBuilder;
import io.github.alathra.raidsperregion.raid.RaidManager;
import io.github.alathra.raidsperregion.raid.area.RaidArea;
import io.github.alathra.raidsperregion.raid.preset.RaidPreset;
import io.github.alathra.raidsperregion.raid.preset.RaidPresetManager;
import io.github.alathra.raidsperregion.raid.tier.RaidTier;
import io.github.alathra.raidsperregion.raid.tier.RaidTierManager;
import io.github.milkdrinkers.colorparser.ColorParser;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class RaidCommand {

    protected RaidCommand() {
        new CommandAPICommand("raid")
            .withFullDescription("RaidsPerRegion raid commands")
            .withShortDescription("RaidsPerRegion raid commands")
            .withSubcommands(
                startCommand(),
                stopCommand(),
                listCommand(),
                helpCommand()
            )
            .executes(this::defaultCommand)
            .register();
    }

    private void sendHelpMenu(CommandSender sender) {
        sender.sendMessage("<gold>[-----RaidsPerRegion4 Help Menu----]");
        sender.sendMessage(ColorParser.of("<green>Start a raid: <yellow>/raid start [type] [world] [area] <preset> <tier> <schedule minutes>").build());
        sender.sendMessage(ColorParser.of("<green>Cancel a raid: <yellow>/raid stop [raid]").build());
        sender.sendMessage(ColorParser.of("<green>View active raids: <yellow>/raid list").build());
    }

    private void defaultCommand(CommandSender sender, CommandArguments args) {
        sendHelpMenu(sender);
    }

    public CommandAPICommand helpCommand() {
        return new CommandAPICommand("help")
            .withPermission(RaidsPerRegion.getAdminPermission())
            .executes((CommandSender sender, CommandArguments args) -> {
                sendHelpMenu(sender);
            });
    }

    public CommandAPICommand startCommand() {
        return new CommandAPICommand("start")
            .withPermission(RaidsPerRegion.getAdminPermission())
            .withArguments(
                CommandUtil.raidTypeArgument("type"),
                CommandUtil.worldArgument("world"),
                CommandUtil.createRaidAreaArgument("area", "type", "world")
            )
            .withOptionalArguments(
                CommandUtil.raidPresetArgument("preset"),
                CommandUtil.raidTierArgument("tier"),
                CommandUtil.raidScheduledArgument("scheduled_minutes")
            )
            .executes((CommandSender sender, CommandArguments args) -> {
                final String argType = (String) args.get("type");
                if (argType == null)
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>Invalid raid type argument").build());
                if (!RaidArea.getTypes().contains(argType))
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>Invalid raid type argument").build());

                final World world = (World) args.get("world");

                final RaidArea raidArea = (RaidArea) args.get("area");

                RaidPresetManager.refreshPresets();
                RaidPreset raidPreset = RaidPresetManager.getDefinedDefaultOrNull();
                if (raidPreset == null) {
                    RaidPresetManager.getPresets().getFirst();
                }
                if (args.getOptional("preset").isPresent()) {
                    raidPreset = (RaidPreset) args.get("preset");
                }

                RaidTierManager.refreshTiers();
                RaidTier raidTier = RaidTierManager.getDefinedDefaultOrNull();
                if (raidTier == null) {
                    RaidTierManager.getTiers().getFirst();
                }
                if (args.getOptional("tier").isPresent()) {
                    raidTier = (RaidTier) args.get("tier");
                }

                Instant raidScheduled = null;
                if (args.getOptional("scheduled_minutes").isPresent()) {
                    raidScheduled = Instant.now().plus(1, ChronoUnit.MINUTES);
                }

                if (world == null)
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>Invalid world argument").build());
                if (raidArea == null)
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>Invalid raid area argument").build());
                if (raidPreset == null)
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>Invalid raid preset argument").build());
                if (raidTier == null)
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>Invalid raid tier argument").build());

                Raid raid = new RaidBuilder()
                    .setStarter(sender)
                    .setWorld(world)
                    .setArea(raidArea)
                    .setPreset(raidPreset)
                    .setTier(raidTier)
                    .setScheduled(raidScheduled)
                    .build();

                raid.schedule();
            });
    }

    public CommandAPICommand stopCommand() {
        return new CommandAPICommand("stop")
            .withPermission(RaidsPerRegion.getAdminPermission())
            .withArguments(
                CommandUtil.raidArgument("raid")
            )
            .executes((CommandSender sender, CommandArguments args) -> {
                final Raid raid = (Raid) args.get("raid");
                if (raid == null)
                    throw CommandAPIBukkit.failWithAdventureComponent(ColorParser.of("<red>Invalid raid argument").build());

                sender.sendMessage(ColorParser.of("<yellow>You have stopped a raid at " + raid.getArea().getName()).build());
                raid.stop();
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
