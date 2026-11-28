package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.sable;

import dev.ftb.mods.ftbskies2aerocompanion.compat.sable.LastKnownPoseHolder;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelOccupancySavedData;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(value = SubLevelContainer.class, remap = false)
public abstract class SubLevelContainerLastKnownPoseMixin implements LastKnownPoseHolder {

    @Unique
    private final Int2ObjectMap<Pose3d> ftbskies2aero$lastKnownPoses = new Int2ObjectOpenHashMap<>();

    @Override
    public Int2ObjectMap<Pose3d> ftbskies2aero$lastKnownPoses() {
        return ftbskies2aero$lastKnownPoses;
    }

    @Inject(method = "allocateSubLevel", at = @At("RETURN"))
    private void ftbskies2aero$recordAllocatedPose(
            UUID uuid, int x, int z, Pose3d pose, CallbackInfoReturnable<SubLevel> cir) {
        SubLevelContainer self = (SubLevelContainer) (Object) this;
        ftbskies2aero$lastKnownPoses.put(self.getIndex(x, z), new Pose3d(pose));
    }

    @Inject(
            method = "removeSubLevel(IILdev/ryanhcode/sable/sublevel/storage/SubLevelRemovalReason;)V",
            at = @At("HEAD"))
    private void ftbskies2aero$recordRemovedPose(int x, int z, SubLevelRemovalReason reason, CallbackInfo ci) {
        SubLevelContainer self = (SubLevelContainer) (Object) this;
        int index = self.getIndex(x, z);
        if (reason == SubLevelRemovalReason.REMOVED) {
            ftbskies2aero$lastKnownPoses.remove(index);
            return;
        }
        SubLevel subLevel = self.getSubLevel(x, z);
        if (subLevel == null) {
            return;
        }
        ftbskies2aero$lastKnownPoses.put(index, new Pose3d(subLevel.logicalPose()));
        if (self.getLevel() instanceof ServerLevel serverLevel) {
            SubLevelOccupancySavedData.getOrLoad(serverLevel).setDirty();
        }
    }
}
