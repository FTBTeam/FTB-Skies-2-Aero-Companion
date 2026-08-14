package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.arsnouveau;

import net.minecraft.core.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(targets = "com.hollingsworth.arsnouveau.common.entity.pathfinding.pathjobs.AbstractPathJob", remap = false)
public abstract class PathJobChunkCacheBoundsMixin {

    private static final Logger FTBSKIES2AERO_LOGGER = LoggerFactory.getLogger("ArsPathJobBounds");

    private static final int FTBSKIES2AERO_MAX_SPAN = 1024;

    private static boolean ftbskies2aero$warned;

    @ModifyArgs(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/hollingsworth/arsnouveau/common/entity/pathfinding/ChunkCache;<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;ILnet/minecraft/world/level/dimension/DimensionType;)V"),
            require = 0, expect = 0)
    private void ftbskies2aero$clampChunkCacheBounds(Args args) {
        BlockPos min = args.get(1);
        BlockPos max = args.get(2);
        if (min == null || max == null) {
            return;
        }
        long spanX = (long) max.getX() - (long) min.getX();
        long spanZ = (long) max.getZ() - (long) min.getZ();
        if (spanX <= FTBSKIES2AERO_MAX_SPAN && spanZ <= FTBSKIES2AERO_MAX_SPAN) {
            return;
        }
        args.set(2, new BlockPos(
                (int) Math.min(max.getX(), (long) min.getX() + FTBSKIES2AERO_MAX_SPAN),
                max.getY(),
                (int) Math.min(max.getZ(), (long) min.getZ() + FTBSKIES2AERO_MAX_SPAN)));
        if (!ftbskies2aero$warned) {
            ftbskies2aero$warned = true;
            FTBSKIES2AERO_LOGGER.warn("Clamped an Ars Nouveau path job spanning {}x{} blocks from {} to {}", spanX, spanZ, min, max);
        }
    }
}
