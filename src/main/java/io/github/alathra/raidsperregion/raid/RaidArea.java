package io.github.alathra.raidsperregion.raid;

public abstract class RaidArea {
    private String name;
    private String type;
    private final Object base;

    private boolean isMobSpawningOnBeforeRaid;

    public RaidArea(Object base) {
        this.base = base;
    }

    protected abstract void setName();
    protected abstract void setType();
    protected abstract void findIfMobSpawningOnBeforeRaid();
    protected abstract void forceMobSpawning();

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Object getBase() {
        return base;
    }
}
