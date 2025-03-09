package io.github.alathra.raidsperregion.raid;

import java.util.Set;

public class RaidManager {

    private static Set<Raid> raids;

    public static boolean registerRaid(Raid newRaid) {
        // You can't have two raids in the same area
        for (Raid raid : raids) {
            if (newRaid.getArea().getBase().equals(raid.getArea().getBase())) {
                return false;
            }
        }

        raids.add(newRaid);
        return true;
    }

    public static void deRegisterRaid(Raid newRaid) {
        newRaid = null;
        raids.remove(newRaid);
    }

    public static Set<Raid> getRaids() {
        return raids;
    }
}
