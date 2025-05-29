package io.github.alathra.raidsperregion.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import io.github.alathra.raidsperregion.RaidsPerRegion;
import io.github.alathra.raidsperregion.Reloadable;

/**
 * A class to handle registration of commands.
 */
public class CommandHandler implements Reloadable {
    private final RaidsPerRegion plugin;

    /**
     * Instantiates the Command handler.
     *
     * @param plugin the plugin
     */
    public CommandHandler(RaidsPerRegion plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(RaidsPerRegion plugin) {
        CommandAPI.onLoad(
            new CommandAPIBukkitConfig(plugin)
                .shouldHookPaperReload(true)
                .silentLogs(true)
                .beLenientForMinorVersions(true)
        );
    }

    @Override
    public void onEnable(RaidsPerRegion plugin) {
        CommandAPI.onEnable();

        // Register commands here
        new RaidCommand();
    }

    @Override
    public void onDisable(RaidsPerRegion plugin) {
        CommandAPI.getRegisteredCommands().forEach(registeredCommand -> CommandAPI.unregister(registeredCommand.namespace() + ':' + registeredCommand.commandName(), true));
        CommandAPI.onDisable();
    }
}