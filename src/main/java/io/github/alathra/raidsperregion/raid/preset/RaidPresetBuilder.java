package io.github.alathra.raidsperregion.raid.preset;

import io.github.alathra.raidsperregion.raid.mob.RaidMob;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RaidPresetBuilder {
    private String name;
    private boolean isDefault;
    private String boss;
    private List<RaidMob> mobs;

    public RaidPresetBuilder() {
        name = "PresetName";
        isDefault = false;
        boss = null;
        mobs = new ArrayList<>();
    }

    public RaidPresetBuilder setName(@NotNull String name) {
        this.name = name;
        return this;
    }

    public RaidPresetBuilder setDefault(@NotNull boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

    public RaidPresetBuilder setBoss(@NotNull String boss) {
        this.boss = boss;
        return this;
    }

    public RaidPresetBuilder setRaidMobs(@NotNull List<RaidMob> mobs) {
        this.mobs = mobs;
        return this;
    }

    public RaidPreset build() {
        return new RaidPreset(name, isDefault, boss, mobs);
    }
}
