package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.ironsspellbooks;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientMagicData.class, remap = false)
public abstract class ClientMagicDataNullGuardMixin {

    @Inject(method = "getSyncedSpellData", at = @At("HEAD"), cancellable = true)
    private static void ftbskies2aero$guardNullMagicData(
            LivingEntity livingEntity, CallbackInfoReturnable<SyncedSpellData> cir) {
        if (livingEntity instanceof IMagicEntity magicEntity && magicEntity.getMagicData() == null) {
            cir.setReturnValue(new SyncedSpellData((LivingEntity) null));
        }
    }
}
