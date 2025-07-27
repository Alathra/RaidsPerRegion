package io.github.alathra.raidsperregion.config;

import io.github.alathra.raidsperregion.raid.preset.RaidPresetManager;
import io.github.milkdrinkers.crate.Config;
import io.github.alathra.raidsperregion.RaidsPerRegion;
import io.github.alathra.raidsperregion.Reloadable;
import io.github.milkdrinkers.crate.ConfigBuilder;
import io.github.milkdrinkers.crate.internal.settings.ReloadSetting;

import javax.inject.Singleton;

/**
 * A class that generates/loads {@literal &} provides access to a configuration file.
 */
@Singleton
public class ConfigHandler implements Reloadable {
    private final RaidsPerRegion plugin;
    private Config cfg;

    public ConfigHandler(RaidsPerRegion plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(RaidsPerRegion plugin) {
        cfg = ConfigBuilder
            .fromPath("config", plugin.getDataFolder().getPath())
            .addInputStream(plugin.getResource("config.yml"))
            .setReloadSetting(ReloadSetting.MANUALLY)
            .create();
    }

    public void reloadConfig() {
        cfg.forceReload();
    }

    @Override
    public void onEnable(RaidsPerRegion plugin) {
        RaidPresetManager.refreshPresets();
    }

    @Override
    public void onDisable(RaidsPerRegion plugin) {
    }

    public Config getConfig() {
        return cfg;
    }
}
