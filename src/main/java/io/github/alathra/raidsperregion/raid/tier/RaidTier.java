package io.github.alathra.raidsperregion.raid.tier;

public class RaidTier {
    private final String name;
    private final boolean isDefault;
    private final int maxMobsAtOnce;
    private final int killsGoal;
    private final int timeLimit;
    private final int mobSpawnsPerCycle;
    private final double cycleRate;

    public RaidTier(String name, boolean isDefault, int maxMobsAtOnce, int killsGoal, int timeLimit, int mobSpawnsPerCycle, double cycleRate) {
        this.name = name;
        this.isDefault = isDefault;
        this.maxMobsAtOnce = maxMobsAtOnce;
        this.killsGoal = killsGoal;
        this.timeLimit = timeLimit;
        this.mobSpawnsPerCycle = mobSpawnsPerCycle;
        this.cycleRate = cycleRate;
    }

    public String getName() {
        return this.name;
    }

    public boolean isDefault() {
        return this.isDefault;
    }

    public int getMaxMobsAtOnce() {
        return maxMobsAtOnce;
    }

    public int getKillsGoal() {
        return killsGoal;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public int getMobSpawnsPerCycle() {
        return mobSpawnsPerCycle;
    }

    public double getCycleRate() {
        return cycleRate;
    }
}
