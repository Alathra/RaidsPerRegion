package io.github.alathra.raidsperregion.hook.factionsuuid;

import io.github.alathra.raidsperregion.RaidsPerRegion;
import io.github.alathra.raidsperregion.hook.AbstractHook;
import io.github.alathra.raidsperregion.hook.Hook;

public class FactionsUUIDHook extends AbstractHook {

    public FactionsUUIDHook(RaidsPerRegion plugin) {
        super(plugin);
    }

    @Override
    public void onEnable(RaidsPerRegion plugin) {
        if (!isHookLoaded())
            return;
    }

    @Override
    public void onDisable(RaidsPerRegion plugin) {
        if (!isHookLoaded())
            return;
    }

    @Override
    public boolean isHookLoaded() {
        return isPluginPresent(Hook.FactionsUUID.getPluginName()) && isPluginEnabled(Hook.FactionsUUID.getPluginName());
    }
}
