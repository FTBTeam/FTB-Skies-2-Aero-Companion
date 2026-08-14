package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.relics;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "it.hurts.sskirillss.relics.entities.relic.midnight_mantle.ConstellationStarEntity", remap = false)
public abstract class ConstellationStarHurtLoopMixin {

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true, remap = true)
    private void ftbskies2aero$skipHurtWhenRemoved(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (((Entity) (Object) this).isRemoved()) {
            cir.setReturnValue(false);
        }
    }
}
