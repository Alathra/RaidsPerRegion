package io.github.alathra.raidsperregion.raid;

import java.util.Set;

public class RaidManager {
    public static Set<Raid> raids;

    public static void registerRaid(Raid newRaid) {
        raids.add(newRaid);
    }

    public static void deRegisterRaid(Raid newRaid) {
        raids.remove(newRaid);
    }
}
