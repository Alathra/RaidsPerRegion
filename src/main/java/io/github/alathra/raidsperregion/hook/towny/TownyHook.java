package io.github.alathra.raidsperregion.hook.towny;

import io.github.alathra.raidsperregion.RaidsPerRegion;
import io.github.alathra.raidsperregion.hook.AbstractHook;
import io.github.alathra.raidsperregion.hook.Hook;

public class TownyHook extends AbstractHook {

    protected TownyHook(RaidsPerRegion plugin) {
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
        return isPluginPresent(Hook.Towny.getPluginName()) && isPluginEnabled(Hook.Towny.getPluginName());
    }
}
