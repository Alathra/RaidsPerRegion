package io.github.alathra.raidsperregion.listener;

import io.github.alathra.raidsperregion.RaidsPerRegion;
import io.github.alathra.raidsperregion.Reloadable;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to handle registration of event listeners.
 */
public class ListenerHandler implements Reloadable {

    private final RaidsPerRegion plugin;
    private final List<Listener> listeners = new ArrayList<>();

    public ListenerHandler(RaidsPerRegion plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(RaidsPerRegion plugin) {
    }

    @Override
    public void onEnable(RaidsPerRegion plugin) {
        listeners.clear(); // Clear the list to avoid duplicate listeners when reloading the plugin
        listeners.add(new MobSpawnListener(plugin));
        listeners.add(new PlayerDeathListener());
        listeners.add(new PvPListener());
        listeners.add(new RaidKillListener());

        // Register listeners here
        for (Listener listener : listeners) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    @Override
    public void onDisable(RaidsPerRegion plugin) {
    }
}
