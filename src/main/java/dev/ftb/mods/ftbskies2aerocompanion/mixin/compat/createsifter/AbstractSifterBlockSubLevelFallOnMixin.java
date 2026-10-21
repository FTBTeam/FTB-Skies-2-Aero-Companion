package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.createsifter;

import com.oierbravo.createsifter.content.contraptions.components.sifter.AbstractSifterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = AbstractSifterBlock.class, remap = false)
public abstract class AbstractSifterBlockSubLevelFallOnMixin {

    @Redirect(
            method = "updateEntityAfterFallOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;blockPosition()Lnet/minecraft/core/BlockPos;"
            )
    )
    private BlockPos ftbskies2aero$useOnPos(Entity entity) {
        return entity.getOnPos();
    }
}
