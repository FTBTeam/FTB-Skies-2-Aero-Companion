package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.createnuclear;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nuclearteam.createnuclear.content.multiblock.controller.ReactorControllerBlockEntity;
import net.nuclearteam.createnuclear.content.multiblock.output.ReactorOutputEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ReactorControllerBlockEntity.class, remap = false)
public abstract class ReactorControllerStarvedShutdownMixin {

    @Shadow
    public ItemStack configuredPattern;

    @Shadow
    private ItemStack fuelItem;

    @Shadow
    private ItemStack coolerItem;

    @Shadow
    public int heat;

    @Shadow
    public abstract void rotate(BlockState state, BlockPos pos, Level level, int rotation, boolean isActif);

    @Shadow
    private static BlockPos FindController(char character) {
        throw new AssertionError();
    }

    @Inject(method = "tick", at = @At("RETURN"), require = 0, expect = 0)
    private void ftbskies2aero$stopWhenStarved(CallbackInfo ci) {
        BlockEntity self = (BlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null || level.isClientSide) {
            return;
        }
        if (configuredPattern == null || configuredPattern.isEmpty()) {
            return;
        }
        if (fuelItem != null && !fuelItem.isEmpty() && coolerItem != null && !coolerItem.isEmpty()) {
            return;
        }

        BlockPos pos = self.getBlockPos();
        BlockPos outputPos = new BlockPos(pos.getX(), pos.getY() + FindController('O').getY(), pos.getZ());
        if (!(level.getBlockEntity(outputPos) instanceof ReactorOutputEntity output) || output.speed == 0) {
            return;
        }

        heat = 0;
        rotate(self.getBlockState(), outputPos, level, 0, false);
    }
}
