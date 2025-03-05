package io.github.alathra.raidsperregion.core.tier;

import org.jetbrains.annotations.NotNull;

public class RaidTierBuilder {
    private String name;
    private boolean isDefault;
    private int maxMobsAtOnce;
    private int killsGoal;
    private int timeLimit;
    private int mobSpawnsPerCycle;
    private double cycleRate;

    public RaidTierBuilder() {
        name = "";
        isDefault = false;
        maxMobsAtOnce = 100;
        killsGoal = 200;
        timeLimit = 600;
        mobSpawnsPerCycle = 1;
        cycleRate = 1.0;
    }

    public RaidTierBuilder setName(@NotNull String name) {
        this.name = name;
        return this;
    }

    public RaidTierBuilder setDefault(@NotNull boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

    public RaidTierBuilder setMaxMobsAtOnce(@NotNull int maxMobsAtOnce) {
        this.maxMobsAtOnce = maxMobsAtOnce;
        return this;
    }

    public RaidTierBuilder setKillsGoal(@NotNull int killsGoal) {
        this.killsGoal = killsGoal;
        return this;
    }

    public RaidTierBuilder setTimeLimit(@NotNull int timeLimit) {
        this.timeLimit = timeLimit;
        return this;
    }

    public RaidTierBuilder setMobSpawnsPerCycle(@NotNull int mobSpawnsPerCycle) {
        this.mobSpawnsPerCycle = mobSpawnsPerCycle;
        return this;
    }

    public RaidTierBuilder setCycleRate(@NotNull double cycleRate) {
        this.cycleRate = cycleRate;
        return this;
    }

    public RaidTier build() {
        // Default cycle rate is 1 second (20 ticks)
        // Since the smallest unit is 1 tick, the maximum cycle rate is 20
        if (cycleRate > 20.0)
            cycleRate = 20.0;

        return new RaidTier(name, isDefault, maxMobsAtOnce, killsGoal, timeLimit, mobSpawnsPerCycle, cycleRate);
    }
}
