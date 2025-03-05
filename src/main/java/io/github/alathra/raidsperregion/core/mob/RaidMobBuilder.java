package io.github.alathra.raidsperregion.core.mob;

import org.jetbrains.annotations.NotNull;

public class RaidMobBuilder {
    private String name;
    private double level;
    private double chance;
    private int weight;

    public RaidMobBuilder() {
        String name = "";
        level = 1.0;
        chance = 0.00;
        weight = 1;
    }

    public RaidMobBuilder setName(@NotNull String name) {
        this.name = name;
        return this;
    }

    public RaidMobBuilder setLevel(@NotNull double level) {
        this.level = level;
        return this;
    }

    public RaidMobBuilder setChance(@NotNull double chance) {
        this.chance = chance;
        return this;
    }

    public RaidMobBuilder setWeight(@NotNull int weight) {
        this.weight = weight;
        return this;
    }

    public RaidMob build() {
        if (level <= 0)
            level = 1.0;
        if (chance < 0.0)
            chance = 0.0;
        return new RaidMob(name, level, chance, weight);
    }

}
