package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.sable;

import dev.ryanhcode.sable.sublevel.storage.HoldingSubLevel;
import dev.ryanhcode.sable.sublevel.storage.holding.GlobalSavedSubLevelPointer;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunk;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunkMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.UUID;

@Mixin(value = SubLevelHoldingChunkMap.class, remap = false)
public abstract class SubLevelSnatchAndLoadMixin {

    @Shadow @Final private Long2ObjectMap<SubLevelHoldingChunk> loadedHoldingChunks;

    @Shadow @Final private LongSet dirtyHoldingChunks;

    @Shadow public abstract void loadHoldingSubLevel(HoldingSubLevel holdingSubLevel);

    @Inject(method = "snatchAndLoad", at = @At("HEAD"), cancellable = true)
    private void ftbskies2aero$snatchFromActualHoldingChunk(GlobalSavedSubLevelPointer pointer, UUID subLevelId, CallbackInfo ci) {
        try {
            SubLevelHoldingChunk pointerChunk = this.loadedHoldingChunks.get(pointer.chunkPos().toLong());
            if (ftbskies2aero$contains(pointerChunk, subLevelId)) {
                return;
            }
            for (SubLevelHoldingChunk chunk : this.loadedHoldingChunks.values()) {
                if (chunk == pointerChunk || !ftbskies2aero$contains(chunk, subLevelId)) {
                    continue;
                }
                Collection<HoldingSubLevel> snatched = ((SubLevelHoldingChunkInvoker) chunk).ftbskies2aero$invokeSnatch(subLevelId);
                if (snatched == null) {
                    return;
                }
                this.dirtyHoldingChunks.add(chunk.getChunkPos().toLong());
                for (HoldingSubLevel holdingSubLevel : snatched) {
                    this.loadHoldingSubLevel(holdingSubLevel);
                }
                ci.cancel();
                return;
            }
        } catch (Throwable ignored) {
        }
    }

    @Unique
    private static boolean ftbskies2aero$contains(SubLevelHoldingChunk chunk, UUID subLevelId) {
        if (chunk == null) {
            return false;
        }
        for (HoldingSubLevel holdingSubLevel : chunk.getLoadedHoldingSubLevels()) {
            if (holdingSubLevel.data().uuid().equals(subLevelId)) {
                return true;
            }
        }
        return false;
    }
}
