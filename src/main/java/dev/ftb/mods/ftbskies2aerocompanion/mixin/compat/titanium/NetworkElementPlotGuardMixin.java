package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.titanium;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.hrznstudio.titanium.block_network.element.NetworkElement", remap = false)
public abstract class NetworkElementPlotGuardMixin {

    @Shadow
    @Final
    protected Level level;

    @Shadow
    @Final
    protected BlockPos pos;

    @Inject(method = "sendBlockUpdate", at = @At("HEAD"), cancellable = true)
    private void ftbskies2aero$skipBlockUpdateInUnloadedPlot(CallbackInfo ci) {
        if (pos == null || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        try {
            SubLevelContainer container = SubLevelContainer.getContainer(serverLevel);
            if (container == null) {
                return;
            }
            ChunkPos chunkPos = new ChunkPos(pos);
            if (container.inBounds(chunkPos) && container.getChunkHolder(chunkPos) == null) {
                ci.cancel();
            }
        } catch (Throwable ignored) {
        }
    }
}
