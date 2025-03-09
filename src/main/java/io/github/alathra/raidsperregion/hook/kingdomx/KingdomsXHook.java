package io.github.alathra.raidsperregion.hook.kingdomx;

import io.github.alathra.raidsperregion.RaidsPerRegion;
import io.github.alathra.raidsperregion.hook.AbstractHook;
import io.github.alathra.raidsperregion.hook.Hook;

public class KingdomsXHook extends AbstractHook {

    public KingdomsXHook(RaidsPerRegion plugin) {
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
        return isPluginPresent(Hook.KingdomsX.getPluginName()) && isPluginEnabled(Hook.KingdomsX.getPluginName());
    }
}
