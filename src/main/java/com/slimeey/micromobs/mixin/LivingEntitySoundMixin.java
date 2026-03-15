package com.slimeey.micromobs.mixin;

import com.slimeey.micromobs.MicroMobsConfig;
import com.slimeey.micromobs.MobScaleManager;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Raises the sound pitch for tiny mobs (Bee, Silverfish, Endermite).
 *
 * getSoundPitch() is the central method all LivingEntity ambient, hurt, and
 * death sounds route through.  Multiplying its return value is the cleanest,
 * safest way to shift pitch without touching individual sound events.
 *
 * require = 0  →  the injection is optional; a changed method signature in a
 * different Yarn build will not crash the game — sounds just stay vanilla pitch.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntitySoundMixin {

    @Inject(
            method    = "getSoundPitch",
            at        = @At("RETURN"),
            cancellable = true,
            require   = 0
    )
    private void micromobs$adjustSoundPitch(CallbackInfoReturnable<Float> cir) {
        if (!MicroMobsConfig.enableSoundAdjustments) return;

        LivingEntity self = (LivingEntity) (Object) this;
        float multiplier = MobScaleManager.getSoundPitchMultiplier(self.getType());

        if (multiplier != 1.0f) {
            cir.setReturnValue(cir.getReturnValue() * multiplier);
        }
    }
}

