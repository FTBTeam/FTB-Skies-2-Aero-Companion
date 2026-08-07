package dev.ftb.mods.ftbskies2aerocompanion.mixin;

import dev.ftb.mods.ftbskies2aerocompanion.compat.sable.SubLevelMoveGuard;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(Block.class)
public abstract class BlockPopResourceGuardMixin {

    @Inject(
            method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void ftbskies2aero$skipPopDuringSubLevelMove(Level level, Supplier<ItemEntity> supplier, ItemStack stack, CallbackInfo ci) {
        if (SubLevelMoveGuard.isActive()) {
            ci.cancel();
        }
    }
}
