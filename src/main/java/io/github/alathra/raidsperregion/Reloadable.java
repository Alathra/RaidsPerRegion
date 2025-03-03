package io.github.alathra.raidsperregion;

/**
 * Implemented in classes that should support being reloaded IE executing the methods during runtime after startup.
 */
public interface Reloadable {
    /**
     * On plugin load.
     */
    void onLoad(RaidsPerRegion plugin);

    /**
     * On plugin enable.
     */
    void onEnable(RaidsPerRegion plugin);

    /**
     * On plugin disable.
     */
    void onDisable(RaidsPerRegion plugin);
}
