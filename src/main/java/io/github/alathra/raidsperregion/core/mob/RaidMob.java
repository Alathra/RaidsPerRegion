package io.github.alathra.raidsperregion.core.mob;

public class RaidMob {
    private final String name;
    private final double level;
    private final double chance;
    private final int weight;

    protected RaidMob(String name, double level, double chance, int weight) {
        this.name = name;
        this.level = level;
        this.chance = chance;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public double getLevel() {
        return level;
    }

    public double getChance() {
        return chance;
    }

    public int getWeight() {
        return weight;
    }
}
