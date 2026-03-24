package com.slimeey.micromobs;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MicroMobs — Realistic Mob Sizes
 * Fabric mod for Minecraft 1.21.11
 *
 * Rescales mobs to closer real-world proportions using the SCALE
 * entity attribute introduced in Minecraft 1.20.5.
 */
public class MicroMobs implements ModInitializer {

    public static final String MOD_ID = "micromobs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        MicroMobsConfig.load();
        MobScaleManager.register();
        LOGGER.info("[MicroMobs] Loaded! Mobs will now spawn at realistic sizes.");
    }
}

