package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.simulatedcoasters;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "dev.silvergold.simulatedcoasters.track.cart.CoasterCartPlotScan", remap = false)
public abstract class CoasterCartPlotScanPaletteSkipMixin {

    @WrapOperation(
            method = {"scanBearingCells", "forEachBearingWorldCell"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;hasOnlyAir()Z"
            ),
            require = 2
    )
    private static boolean ftbskies2aero$skipSectionsWithoutBlock(LevelChunkSection section, Operation<Boolean> original, @Local(argsOnly = true) Block block) {
        return original.call(section) || !section.maybeHas(state -> state.is(block));
    }
}
