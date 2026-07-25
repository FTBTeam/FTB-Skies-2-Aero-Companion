package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.ftbteambases;

import dev.ftb.mods.ftbskies2aerocompanion.basebuffer.BaseExclusionConfig;
import dev.ftb.mods.ftbskies2aerocompanion.basebuffer.TeamBaseGrid;
import dev.ftb.mods.ftbteambases.data.bases.BaseInstanceManager;
import dev.ftb.mods.ftbteambases.data.definition.BaseDefinition;
import dev.ftb.mods.ftbteambases.util.RegionCoords;
import dev.ftb.mods.ftbteambases.util.RegionExtents;
import dev.ftb.mods.ftblibrary.math.XZ;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = BaseInstanceManager.class, remap = false)
public abstract class BaseInstanceManagerMCAScanMixin {

    @Unique
    private int ftbskies2aero$spiralCursor;

    @Inject(method = "nextGenerationPos", at = @At("HEAD"))
    private void ftbskies2aero$resetSpiral(MinecraftServer server, BaseDefinition definition, ResourceLocation dimensionId, XZ size,
                                           CallbackInfoReturnable<RegionCoords> cir) {
        ftbskies2aero$spiralCursor = 0;
    }

    @Inject(method = "getNextRegionCoords", at = @At("HEAD"), cancellable = true)
    private void ftbskies2aero$spiralCandidates(ResourceLocation dimensionId, XZ size, CallbackInfoReturnable<RegionCoords> cir) {
        try {
            if (ftbskies2aero$spiralCursor < TeamBaseGrid.totalBaseSlots()) {
                int[] r = TeamBaseGrid.nthBaseRegion(ftbskies2aero$spiralCursor++);
                cir.setReturnValue(new RegionCoords(r[0], r[1]));
            }
        } catch (Throwable ignored) {
        }
    }

    @Inject(method = "anyMCAFilesPresent", at = @At("HEAD"), cancellable = true)
    private void ftbskies2aero$occupiedByRecordedBasesOnly(MinecraftServer server, ResourceLocation dimensionId, RegionCoords start, XZ size,
                                                           CallbackInfoReturnable<Boolean> cir) {
        BaseInstanceManager self = (BaseInstanceManager) (Object) this;
        int minX = start.x();
        int minZ = start.z();
        int maxX = minX + Math.max(1, size.x()) - 1;
        int maxZ = minZ + Math.max(1, size.z()) - 1;

        int reserved = BaseExclusionConfig.SPAWN_RESERVED_RADIUS.get();
        if (reserved > 0
                && minX * 512 <= reserved && (maxX + 1) * 512 - 1 >= -reserved
                && minZ * 512 <= reserved && (maxZ + 1) * 512 - 1 >= -reserved) {
            cir.setReturnValue(true);
            return;
        }

        List<RegionExtents> occupied = new ArrayList<>();
        self.allLiveBases().values().forEach(base -> {
            if (base.dimension().location().equals(dimensionId)) {
                occupied.add(base.extents());
            }
        });
        self.getArchivedBases().forEach(base -> occupied.add(base.extents()));

        for (RegionExtents extents : occupied) {
            if (extents.start().x() <= maxX && extents.end().x() >= minX
                    && extents.start().z() <= maxZ && extents.end().z() >= minZ) {
                cir.setReturnValue(true);
                return;
            }
        }

        if (BaseExclusionConfig.LEGACY_WORLD_MODE.get() && (minX < 0 || minZ < 0)) {
            return;
        }
        cir.setReturnValue(false);
    }
}
