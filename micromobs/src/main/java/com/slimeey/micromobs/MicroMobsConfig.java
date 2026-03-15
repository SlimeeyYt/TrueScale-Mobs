package com.slimeey.micromobs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads and saves micromobs.json in the Minecraft config directory.
 *
 * All scale values are expressed as a multiplier relative to the vanilla size.
 * 1.0 = vanilla size, 0.25 = 25% of vanilla size, etc.
 *
 * On first launch the file is created with the default values below.
 * Any missing keys in an existing file keep their coded defaults.
 */
public class MicroMobsConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("micromobs.json");

    // -----------------------------------------------------------------------
    // Small Creatures — major downscale
    // -----------------------------------------------------------------------
    public static double spiderScale     = 0.25;
    public static double caveSpiderScale = 0.22;
    public static double beeScale        = 0.18;
    public static double silverfishScale = 0.10;
    public static double endermiteScale  = 0.10;
    public static double frogScale       = 0.35;
    public static double tadpoleScale    = 0.20;

    // -----------------------------------------------------------------------
    // Small Animals — moderate downscale
    // -----------------------------------------------------------------------
    public static double chickenScale = 0.75;
    public static double rabbitScale  = 0.60;
    public static double foxScale     = 0.70;
    public static double catScale     = 0.70;
    public static double ocelotScale  = 0.70;
    public static double parrotScale  = 0.65;

    // -----------------------------------------------------------------------
    // Medium Animals — slight downscale
    // -----------------------------------------------------------------------
    public static double pigScale   = 0.90;
    public static double sheepScale = 0.90;
    public static double cowScale   = 0.95;
    public static double goatScale  = 0.90;
    public static double wolfScale  = 0.90;
    public static double pandaScale = 0.95;

    // -----------------------------------------------------------------------
    // Aquatic Creatures
    // -----------------------------------------------------------------------
    public static double codScale          = 0.80;
    public static double salmonScale       = 0.90;
    public static double pufferfishScale   = 0.40;
    public static double tropicalFishScale = 0.70;
    public static double squidScale        = 0.80;
    public static double glowSquidScale    = 0.80;
    public static double axolotlScale      = 0.50;
    public static double turtleScale       = 0.80;
    public static double dolphinScale      = 0.95;

    // -----------------------------------------------------------------------
    // Special — base scale reduced 30 % so sizes stay manageable
    // -----------------------------------------------------------------------
    public static double slimeScale     = 0.70;
    public static double magmaCubeScale = 0.70;

    // -----------------------------------------------------------------------
    // Feature toggles
    // -----------------------------------------------------------------------
    public static boolean enableMovementAdjustments = true;
    public static boolean enableSoundAdjustments    = true;

    // -----------------------------------------------------------------------
    // Load / Save
    // -----------------------------------------------------------------------

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                if (data != null) applyData(data);
            } catch (IOException e) {
                MicroMobs.LOGGER.error("[MicroMobs] Failed to read config, using defaults.", e);
            }
        } else {
            save(); // write defaults on first launch
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(buildData(), writer);
            }
        } catch (IOException e) {
            MicroMobs.LOGGER.error("[MicroMobs] Failed to write config.", e);
        }
    }

    // Apply values that are present in the JSON (null = field absent, keep default)
    private static void applyData(ConfigData d) {
        if (d.spiderScale     != null) spiderScale     = d.spiderScale;
        if (d.caveSpiderScale != null) caveSpiderScale = d.caveSpiderScale;
        if (d.beeScale        != null) beeScale        = d.beeScale;
        if (d.silverfishScale != null) silverfishScale = d.silverfishScale;
        if (d.endermiteScale  != null) endermiteScale  = d.endermiteScale;
        if (d.frogScale       != null) frogScale       = d.frogScale;
        if (d.tadpoleScale    != null) tadpoleScale    = d.tadpoleScale;

        if (d.chickenScale != null) chickenScale = d.chickenScale;
        if (d.rabbitScale  != null) rabbitScale  = d.rabbitScale;
        if (d.foxScale     != null) foxScale     = d.foxScale;
        if (d.catScale     != null) catScale     = d.catScale;
        if (d.ocelotScale  != null) ocelotScale  = d.ocelotScale;
        if (d.parrotScale  != null) parrotScale  = d.parrotScale;

        if (d.pigScale   != null) pigScale   = d.pigScale;
        if (d.sheepScale != null) sheepScale = d.sheepScale;
        if (d.cowScale   != null) cowScale   = d.cowScale;
        if (d.goatScale  != null) goatScale  = d.goatScale;
        if (d.wolfScale  != null) wolfScale  = d.wolfScale;
        if (d.pandaScale != null) pandaScale = d.pandaScale;

        if (d.codScale          != null) codScale          = d.codScale;
        if (d.salmonScale       != null) salmonScale       = d.salmonScale;
        if (d.pufferfishScale   != null) pufferfishScale   = d.pufferfishScale;
        if (d.tropicalFishScale != null) tropicalFishScale = d.tropicalFishScale;
        if (d.squidScale        != null) squidScale        = d.squidScale;
        if (d.glowSquidScale    != null) glowSquidScale    = d.glowSquidScale;
        if (d.axolotlScale      != null) axolotlScale      = d.axolotlScale;
        if (d.turtleScale       != null) turtleScale       = d.turtleScale;
        if (d.dolphinScale      != null) dolphinScale      = d.dolphinScale;

        if (d.slimeScale     != null) slimeScale     = d.slimeScale;
        if (d.magmaCubeScale != null) magmaCubeScale = d.magmaCubeScale;

        if (d.enableMovementAdjustments != null) enableMovementAdjustments = d.enableMovementAdjustments;
        if (d.enableSoundAdjustments    != null) enableSoundAdjustments    = d.enableSoundAdjustments;
    }

    private static ConfigData buildData() {
        ConfigData d = new ConfigData();
        d.spiderScale     = spiderScale;
        d.caveSpiderScale = caveSpiderScale;
        d.beeScale        = beeScale;
        d.silverfishScale = silverfishScale;
        d.endermiteScale  = endermiteScale;
        d.frogScale       = frogScale;
        d.tadpoleScale    = tadpoleScale;

        d.chickenScale = chickenScale;
        d.rabbitScale  = rabbitScale;
        d.foxScale     = foxScale;
        d.catScale     = catScale;
        d.ocelotScale  = ocelotScale;
        d.parrotScale  = parrotScale;

        d.pigScale   = pigScale;
        d.sheepScale = sheepScale;
        d.cowScale   = cowScale;
        d.goatScale  = goatScale;
        d.wolfScale  = wolfScale;
        d.pandaScale = pandaScale;

        d.codScale          = codScale;
        d.salmonScale       = salmonScale;
        d.pufferfishScale   = pufferfishScale;
        d.tropicalFishScale = tropicalFishScale;
        d.squidScale        = squidScale;
        d.glowSquidScale    = glowSquidScale;
        d.axolotlScale      = axolotlScale;
        d.turtleScale       = turtleScale;
        d.dolphinScale      = dolphinScale;

        d.slimeScale     = slimeScale;
        d.magmaCubeScale = magmaCubeScale;

        d.enableMovementAdjustments = enableMovementAdjustments;
        d.enableSoundAdjustments    = enableSoundAdjustments;
        return d;
    }

    // -----------------------------------------------------------------------
    // POJO used by Gson — nullable fields = optional in the JSON file
    // -----------------------------------------------------------------------
    @SuppressWarnings("unused")
    private static class ConfigData {
        // Small Creatures
        Double spiderScale;
        Double caveSpiderScale;
        Double beeScale;
        Double silverfishScale;
        Double endermiteScale;
        Double frogScale;
        Double tadpoleScale;
        // Small Animals
        Double chickenScale;
        Double rabbitScale;
        Double foxScale;
        Double catScale;
        Double ocelotScale;
        Double parrotScale;
        // Medium Animals
        Double pigScale;
        Double sheepScale;
        Double cowScale;
        Double goatScale;
        Double wolfScale;
        Double pandaScale;
        // Aquatic
        Double codScale;
        Double salmonScale;
        Double pufferfishScale;
        Double tropicalFishScale;
        Double squidScale;
        Double glowSquidScale;
        Double axolotlScale;
        Double turtleScale;
        Double dolphinScale;
        // Special
        Double slimeScale;
        Double magmaCubeScale;
        // Toggles
        Boolean enableMovementAdjustments;
        Boolean enableSoundAdjustments;
    }
}

