package io.github.alathra.raidsperregion.hook;

import io.github.alathra.raidsperregion.RaidsPerRegion;
import io.github.alathra.raidsperregion.Reloadable;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

public class AbstractHook implements Reloadable {
    private final RaidsPerRegion plugin;

    protected AbstractHook(RaidsPerRegion plugin) {
        this.plugin = plugin;
    }

    /**
     * Get the plugin instance.
     * @return plugin instance
     */
    protected RaidsPerRegion getPlugin() {
        return plugin;
    }

    /**
     * Check if this hook is loaded and ready for use.
     *
     * @return boolean whether this hook is loaded or not
     * @implNote This check is a guarantee that the hook and its dependencies have loaded properly and are ready for usage.
     */
    public boolean isHookLoaded() {
        throw new UnsupportedOperationException("Method isHookLoaded() is not implemented");
    }

    /**
     * On plugin load.
     */
    @Override
    public void onLoad(RaidsPerRegion plugin) {
    }

    /**
     * On plugin enable.
     */
    @Override
    public void onEnable(RaidsPerRegion plugin) {
    }

    /**
     * On plugin disable.
     */
    @Override
    public void onDisable(RaidsPerRegion plugin) {
    }

    /**
     * Check if a plugin is present on the server.
     *
     * @param pluginName the plugin name
     * @return boolean whether the plugin is present or not
     * @implNote This check is a guarantee that the plugin is present on the server and that the pluginName is not null.
     */
    public static boolean isPluginPresent(@Nullable String pluginName) {
        return pluginName != null && Bukkit.getPluginManager().getPlugin(pluginName) != null;
    }

    /**
     * Check if a plugin is enabled on the server.
     *
     * @param pluginName the plugin name
     * @return boolean whether the plugin is enabled or not
     * @implNote This check is a guarantee that the plugin is enabled on the server and that the pluginName is not null.
     */
    public static boolean isPluginEnabled(@Nullable String pluginName) {
        return pluginName != null && Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }
}
