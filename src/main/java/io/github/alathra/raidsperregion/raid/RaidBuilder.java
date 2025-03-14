package io.github.alathra.raidsperregion.raid;

import io.github.alathra.raidsperregion.raid.area.RaidArea;
import io.github.alathra.raidsperregion.raid.preset.RaidPreset;
import io.github.alathra.raidsperregion.raid.tier.RaidTier;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public class RaidBuilder {
    // -- PARAMETERS --
    // The person who started the raid
    private CommandSender starter;
    // The world that the raid is happening in
    private World world;
    // The mobs and spawn chances
    private RaidPreset preset;
    // The tier of the raid, which stores relevant settings
    private RaidTier tier;
    // The target area (region, town, etc.)
    private RaidArea area;
    // The scheduled time of the raid
    private Instant scheduled;

    public RaidBuilder() {
    }

    public RaidBuilder setStarter(CommandSender starter) {
        this.starter = starter;
        return this;
    }

    public RaidBuilder setWorld(@NotNull World world) {
        this.world = world;
        return this;
    }

    public RaidBuilder setArea(@NotNull RaidArea area) {
        this.area = area;
        return this;
    }

    public RaidBuilder setPreset(@NotNull RaidPreset preset) {
        this.preset = preset;
        return this;
    }

    public RaidBuilder setTier(@NotNull RaidTier tier) {
        this.tier = tier;
        return this;
    }

    public RaidBuilder setScheduled(@Nullable Instant scheduled) {
        this.scheduled = scheduled;
        return this;
    }


    public Raid build() {
        return new Raid(starter, world, area, preset, tier, scheduled);
    }
}
