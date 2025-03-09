package io.github.alathra.raidsperregion.raid.preset;

import io.github.alathra.raidsperregion.config.Settings;
import io.github.alathra.raidsperregion.utility.Logger;

import java.util.ArrayList;
import java.util.List;

public class RaidPresetManager {

    public static List<RaidPreset> raidPresets = new ArrayList<>();

    public static void refreshPresets() {
        raidPresets.clear();
        Settings.getRaidPresets().forEach(RaidPresetManager::registerPreset);
    }

    public static void registerPreset(RaidPreset raidPreset) {
        deregisterPreset(raidPreset);
        if (raidPreset.isDefault() && getDefinedDefaultOrNull() != null) {
           raidPreset = new RaidPresetBuilder()
               .setName(raidPreset.getName())
               .setBoss(raidPreset.getBoss())
               .setDefault(false)
               .setRaidMobs(raidPreset.getMobs())
               .build();
           Logger.get().warn("Preset Register Warning: Multiple presets set as default. This may produce unexpected behavior.");
        }
        raidPresets.add(raidPreset);
    }

    public static void deregisterPreset(RaidPreset raidPreset) {
        RaidPreset matching = findMatchingNameOrNull(raidPreset);
        if (matching != null) {
            raidPresets.remove(matching);
        }
    }

    public static RaidPreset findMatchingNameOrNull(RaidPreset newRaidPreset) {
        for (RaidPreset raidPreset : raidPresets) {
            if (newRaidPreset.getName().equalsIgnoreCase(raidPreset.getName())) {
                return raidPreset;
            }
        }
        return null;
    }

    public static RaidPreset getDefinedDefaultOrNull() {
        for (RaidPreset raidPreset : raidPresets) {
            if (raidPreset.isDefault()) {
                return raidPreset;
            }
        }
        return null;
    }

    public static List<RaidPreset> getPresets() {
        return raidPresets;
    }
}
