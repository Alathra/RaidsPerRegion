package io.github.alathra.raidsperregion.raid;

import java.util.HashSet;
import java.util.Set;

public class RaidManager {

    private static final Set<Raid> raids = new HashSet<>();

    public static boolean registerRaid(Raid newRaid) {
        raids.add(newRaid);
        return true;
    }

    public static void deRegisterRaid(Raid newRaid) {
        raids.remove(newRaid);
    }

    public static Set<Raid> getRaids() {
        return raids;
    }
}
