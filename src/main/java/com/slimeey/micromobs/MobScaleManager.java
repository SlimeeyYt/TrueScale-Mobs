package com.slimeey.micromobs;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;

/**
 * MobScaleManager
 *
 * Registers the ENTITY_LOAD event and applies two attribute modifiers to
 * qualifying mobs:
 *
 *   1. SCALE  — changes the visual model, hitbox, eye-height and
 *                       shadow size proportionally.  Introduced in MC 1.20.5.
 *
 *   2. MOVEMENT_SPEED / FLYING_SPEED  — small speed bump for
 *                       tiny creatures so they remain a threat.
 *
 * Both modifiers are added with addPersistentModifier(), meaning they are
 * stored in the entity's NBT data.  The load event therefore only fires the
 * actual attribute write once per entity; on subsequent world-loads the
 * modifier already exists and the guard check (getModifier == null) skips it.
 */
public class MobScaleManager {

    /** Identifier for the scale attribute modifier stored on each mob. */
    private static final Identifier SCALE_MOD_ID =
            Identifier.of(MicroMobs.MOD_ID, "mob_scale");

    /** Identifier for the speed attribute modifier stored on each mob. */
    private static final Identifier SPEED_MOD_ID =
            Identifier.of(MicroMobs.MOD_ID, "mob_speed");

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(entity instanceof LivingEntity living)) return;

            EntityAttributeInstance scaleAttr =
                    living.getAttributeInstance(EntityAttributes.SCALE);

            // Guard: only apply once (persistent modifier is saved in NBT)
            if (scaleAttr == null || scaleAttr.getModifier(SCALE_MOD_ID) != null) return;

            double targetScale = getScale(living);
            if (targetScale == 1.0) return;   // no change needed

            applyScale(living, targetScale);
        });
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    private static void applyScale(LivingEntity entity, double targetScale) {

        // ---- 1. Scale attribute -----------------------------------------
        // Operation ADD_VALUE:  finalValue = baseValue(1.0) + modifier
        // So modifier = targetScale - 1.0  →  finalValue = targetScale  ✓
        EntityAttributeInstance scaleAttr =
                entity.getAttributeInstance(EntityAttributes.SCALE);
        if (scaleAttr != null) {
            scaleAttr.addPersistentModifier(new EntityAttributeModifier(
                    SCALE_MOD_ID,
                    targetScale - 1.0,
                    EntityAttributeModifier.Operation.ADD_VALUE
            ));
        }

        // ---- 2. Movement speed adjustment (optional) --------------------
        if (MicroMobsConfig.enableMovementAdjustments) {
            double speedBoost = getSpeedBoost(entity);
            if (speedBoost != 0.0) {
                applySpeedBoost(entity, speedBoost);
            }
        }
    }

    /**
     * Applies a percentage speed boost using ADD_MULTIPLIED_BASE so the
     * modifier stacks naturally with vanilla base values.
     * speedBoost = 0.2 means "+20 % of base speed".
     */
    private static void applySpeedBoost(LivingEntity entity, double speedBoost) {
        // Ground movement speed
        EntityAttributeInstance speedAttr =
                entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (speedAttr != null && speedAttr.getModifier(SPEED_MOD_ID) == null) {
            speedAttr.addPersistentModifier(new EntityAttributeModifier(
                    SPEED_MOD_ID,
                    speedBoost,
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
            ));
        }

        // Flying speed (Bees, Parrots etc.)
        EntityAttributeInstance flyAttr =
                entity.getAttributeInstance(EntityAttributes.FLYING_SPEED);
        if (flyAttr != null && flyAttr.getModifier(SPEED_MOD_ID) == null) {
            flyAttr.addPersistentModifier(new EntityAttributeModifier(
                    SPEED_MOD_ID,
                    speedBoost,
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
            ));
        }
    }

    // ------------------------------------------------------------------
    // Scale lookup table
    // ------------------------------------------------------------------

    public static double getScale(LivingEntity entity) {
        EntityType<?> t = entity.getType();

        // --- Small Creatures (major downscale) ---
        if (t == EntityType.SPIDER)      return MicroMobsConfig.spiderScale;
        if (t == EntityType.CAVE_SPIDER) return MicroMobsConfig.caveSpiderScale;
        if (t == EntityType.BEE)         return MicroMobsConfig.beeScale;
        if (t == EntityType.SILVERFISH)  return MicroMobsConfig.silverfishScale;
        if (t == EntityType.ENDERMITE)   return MicroMobsConfig.endermiteScale;
        if (t == EntityType.FROG)        return MicroMobsConfig.frogScale;
        if (t == EntityType.TADPOLE)     return MicroMobsConfig.tadpoleScale;

        // --- Small Animals (moderate downscale) ---
        if (t == EntityType.CHICKEN) return MicroMobsConfig.chickenScale;
        if (t == EntityType.RABBIT)  return MicroMobsConfig.rabbitScale;
        if (t == EntityType.FOX)     return MicroMobsConfig.foxScale;
        if (t == EntityType.CAT)     return MicroMobsConfig.catScale;
        if (t == EntityType.OCELOT)  return MicroMobsConfig.ocelotScale;
        if (t == EntityType.PARROT)  return MicroMobsConfig.parrotScale;

        // --- Medium Animals (slight downscale) ---
        if (t == EntityType.PIG)   return MicroMobsConfig.pigScale;
        if (t == EntityType.SHEEP) return MicroMobsConfig.sheepScale;
        if (t == EntityType.COW)   return MicroMobsConfig.cowScale;
        if (t == EntityType.GOAT)  return MicroMobsConfig.goatScale;
        if (t == EntityType.WOLF)  return MicroMobsConfig.wolfScale;
        if (t == EntityType.PANDA) return MicroMobsConfig.pandaScale;

        // --- Aquatic ---
        if (t == EntityType.COD)           return MicroMobsConfig.codScale;
        if (t == EntityType.SALMON)        return MicroMobsConfig.salmonScale;
        if (t == EntityType.PUFFERFISH)    return MicroMobsConfig.pufferfishScale;
        if (t == EntityType.TROPICAL_FISH) return MicroMobsConfig.tropicalFishScale;
        if (t == EntityType.SQUID)         return MicroMobsConfig.squidScale;
        if (t == EntityType.GLOW_SQUID)    return MicroMobsConfig.glowSquidScale;
        if (t == EntityType.AXOLOTL)       return MicroMobsConfig.axolotlScale;
        if (t == EntityType.TURTLE)        return MicroMobsConfig.turtleScale;
        if (t == EntityType.DOLPHIN)       return MicroMobsConfig.dolphinScale;

        // --- Slime / Magma Cube (reduce base 30 %) ---
        if (t == EntityType.SLIME)      return MicroMobsConfig.slimeScale;
        if (t == EntityType.MAGMA_CUBE) return MicroMobsConfig.magmaCubeScale;

        // --- Monsters intentionally kept at vanilla 1.0 ---
        // Skeleton, Zombie, Creeper, Enderman, Witch, Evoker, Vindicator
        // --- Large creatures also 1.0 ---
        // Iron Golem, Ravager, Warden, Ender Dragon, Wither, Guardian, Elder Guardian
        return 1.0;
    }

    /**
     * Returns the ADD_MULTIPLIED_BASE speed delta for tiny, fast mobs.
     * 0.0 means no change; 0.2 means +20 % on top of base speed.
     */
    public static double getSpeedBoost(LivingEntity entity) {
        EntityType<?> t = entity.getType();
        if (t == EntityType.SPIDER)     return 0.20; // ×1.20
        if (t == EntityType.SILVERFISH) return 0.30; // ×1.30
        if (t == EntityType.ENDERMITE)  return 0.30; // ×1.30
        if (t == EntityType.BEE)        return 0.15; // ×1.15
        return 0.0;
    }

    /**
     * Returns the sound-pitch multiplier for tiny mobs.
     * 1.0 means no change.
     */
    public static float getSoundPitchMultiplier(EntityType<?> type) {
        if (type == EntityType.BEE)        return 1.20f;
        if (type == EntityType.SILVERFISH) return 1.30f;
        if (type == EntityType.ENDERMITE)  return 1.30f;
        return 1.0f;
    }
}

