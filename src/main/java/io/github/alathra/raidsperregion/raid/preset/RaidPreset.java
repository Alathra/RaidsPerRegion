package io.github.alathra.raidsperregion.raid.preset;

import io.github.alathra.raidsperregion.raid.mob.RaidMob;

import java.util.List;

public class RaidPreset {
    private final String name;
    private final boolean isDefault;
    private final String boss;
    private final List<RaidMob> mobs;

    protected RaidPreset(String name, boolean isDefault, String boss, List<RaidMob> mobs) {
        this.name = name;
        this.isDefault = isDefault;
        this.boss = boss;
        this.mobs = mobs;
    }

    public String getName() {
        return name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public String getBoss() {
        return boss;
    }

    public List<RaidMob> getMobs() {
        return mobs;
    }

    public boolean hasBoss() {
        return (boss != null && !boss.isBlank());
    }
}
