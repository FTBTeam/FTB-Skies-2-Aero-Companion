package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.tropicraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.tropicraft.core.common.dimension.feature.tree.RainforestTreeFeature", remap = false)
public abstract class RainforestTreeMahoganyLeavesMixin {

    private static final ResourceLocation MAHOGANY_LEAVES =
            ResourceLocation.fromNamespaceAndPath("tropicraft", "mahogany_leaves");

    @ModifyReturnValue(method = "getLeaf", at = @At("RETURN"))
    private BlockState ftbskies2aero$mahoganyLeaves(BlockState original) {
        Block leaves = BuiltInRegistries.BLOCK.get(MAHOGANY_LEAVES);
        return leaves == Blocks.AIR ? original : leaves.withPropertiesOf(original);
    }
}
