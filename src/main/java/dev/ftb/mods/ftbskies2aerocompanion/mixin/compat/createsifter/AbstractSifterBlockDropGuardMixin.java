package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.createsifter;

import com.oierbravo.createsifter.content.contraptions.components.sifter.AbstractSifterBlock;
import dev.ftb.mods.ftbskies2aerocompanion.compat.sable.SubLevelMoveGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(value = AbstractSifterBlock.class, remap = false)
public abstract class AbstractSifterBlockDropGuardMixin {

    @Redirect(
            method = "onRemove",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/oierbravo/createsifter/content/contraptions/components/sifter/AbstractSifterBlock;withBlockEntityDo(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Ljava/util/function/Consumer;)V"
            )
    )
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void ftbskies2aero$skipDropsDuringSubLevelMove(AbstractSifterBlock self, BlockGetter world, BlockPos pos, Consumer action) {
        if (SubLevelMoveGuard.isActive()) {
            return;
        }
        self.withBlockEntityDo(world, pos, action);
    }
}
