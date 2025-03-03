package io.github.alathra.raidsperregion.utility;


import io.github.alathra.raidsperregion.RaidsPerRegion;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

/**
 * A class that provides shorthand access to {@link RaidsPerRegion#getComponentLogger}.
 */
public class Logger {
    /**
     * Get component logger. Shorthand for:
     *
     * @return the component logger {@link RaidsPerRegion#getComponentLogger}.
     */
    @NotNull
    public static ComponentLogger get() {
        return RaidsPerRegion.getInstance().getComponentLogger();
    }
}
