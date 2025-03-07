package io.github.alathra.raidsperregion;

import io.github.alathra.raidsperregion.hook.HookManager;
import io.github.alathra.raidsperregion.command.CommandHandler;
import io.github.alathra.raidsperregion.config.ConfigHandler;
import io.github.alathra.raidsperregion.listener.ListenerHandler;
import io.github.alathra.raidsperregion.updatechecker.UpdateHandler;

import io.github.alathra.raidsperregion.utility.Logger;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class RaidsPerRegion extends JavaPlugin {

    private static RaidsPerRegion instance;
    private static ScoreboardLibrary scoreboardLibrary;

    // Handlers/Managers
    private ConfigHandler configHandler;
    private HookManager hookManager;
    private CommandHandler commandHandler;
    private ListenerHandler listenerHandler;
    private UpdateHandler updateHandler;

    // Handlers list (defines order of load/enable/disable)
    private List<? extends Reloadable> handlers;

    public static RaidsPerRegion getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;

        configHandler = new ConfigHandler(this);
        hookManager = new HookManager(this);
        commandHandler = new CommandHandler(this);
        listenerHandler = new ListenerHandler(this);
        updateHandler = new UpdateHandler(this);

        handlers = List.of(
            configHandler,
            hookManager,
            commandHandler,
            listenerHandler,
            updateHandler
        );

        for (Reloadable handler : handlers)
            handler.onLoad(instance);
    }

    @Override
    public void onEnable() {
        for (Reloadable handler : handlers)
            handler.onEnable(instance);

        // Initialize scoreboard library
        try {
            scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(this);
        } catch (NoPacketAdapterAvailableException e) {
            // If no packet adapter was found, you can fallback to the no-op implementation:
            scoreboardLibrary = new NoopScoreboardLibrary();
            Logger.get().warn("No scoreboard packet adapter available!");
        }

    }

    @Override
    public void onDisable() {
        for (Reloadable handler : handlers.reversed()) // If reverse doesn't work implement a new List with your desired disable order
            handler.onDisable(instance);

        scoreboardLibrary.close();
    }

    public void onReload() {
        onDisable();
        onLoad();
        onEnable();
    }

    @NotNull
    public ConfigHandler getConfigHandler() {
        return configHandler;
    }


    @NotNull
    public HookManager getHookManager() {
        return hookManager;
    }

    @NotNull
    public UpdateHandler getUpdateHandler() {
        return updateHandler;
    }

    @NotNull
    public ScoreboardLibrary getScoreboardLibrary() {
        return scoreboardLibrary;
    }
}
