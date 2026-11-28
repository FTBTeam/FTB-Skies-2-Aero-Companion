package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.sable;

import dev.ftb.mods.ftbskies2aerocompanion.compat.sable.LastKnownPoseHolder;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelOccupancySavedData;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SubLevelOccupancySavedData.class, remap = false)
public abstract class SubLevelOccupancyLastKnownPoseMixin {

    @Unique
    private static final String FTBSKIES2AERO$KEY = "ironssablecompat:last_known_poses";

    @Shadow
    @Final
    private ServerLevel level;

    @Inject(method = "save", at = @At("RETURN"))
    private void ftbskies2aero$saveLastKnownPoses(
            CompoundTag tag, HolderLookup.Provider provider, CallbackInfoReturnable<CompoundTag> cir) {
        SubLevelContainer container = SubLevelContainer.getContainer(level);
        if (!(container instanceof LastKnownPoseHolder holder)) {
            return;
        }
        Int2ObjectMap<Pose3d> poses = holder.ftbskies2aero$lastKnownPoses();
        for (SubLevel subLevel : container.getAllSubLevels()) {
            ChunkPos plotPos = subLevel.getPlot().plotPos;
            int index = container.getIndex(
                    plotPos.x - container.getOrigin().x,
                    plotPos.z - container.getOrigin().y);
            poses.put(index, new Pose3d(subLevel.logicalPose()));
        }
        ListTag list = new ListTag();
        for (Int2ObjectMap.Entry<Pose3d> entry : poses.int2ObjectEntrySet()) {
            Pose3d pose = entry.getValue();
            CompoundTag poseTag = new CompoundTag();
            poseTag.putInt("i", entry.getIntKey());
            poseTag.putDouble("px", pose.position().x());
            poseTag.putDouble("py", pose.position().y());
            poseTag.putDouble("pz", pose.position().z());
            poseTag.putDouble("ox", pose.orientation().x());
            poseTag.putDouble("oy", pose.orientation().y());
            poseTag.putDouble("oz", pose.orientation().z());
            poseTag.putDouble("ow", pose.orientation().w());
            poseTag.putDouble("rx", pose.rotationPoint().x());
            poseTag.putDouble("ry", pose.rotationPoint().y());
            poseTag.putDouble("rz", pose.rotationPoint().z());
            poseTag.putDouble("sx", pose.scale().x());
            poseTag.putDouble("sy", pose.scale().y());
            poseTag.putDouble("sz", pose.scale().z());
            list.add(poseTag);
        }
        tag.put(FTBSKIES2AERO$KEY, list);
    }

    @Inject(method = "load", at = @At("RETURN"))
    private static void ftbskies2aero$loadLastKnownPoses(
            ServerLevel level, CompoundTag tag, CallbackInfoReturnable<SubLevelOccupancySavedData> cir) {
        SubLevelContainer container = SubLevelContainer.getContainer(level);
        if (!(container instanceof LastKnownPoseHolder holder) || !tag.contains(FTBSKIES2AERO$KEY, Tag.TAG_LIST)) {
            return;
        }
        Int2ObjectMap<Pose3d> poses = holder.ftbskies2aero$lastKnownPoses();
        ListTag list = tag.getList(FTBSKIES2AERO$KEY, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag poseTag = list.getCompound(i);
            poses.put(
                    poseTag.getInt("i"),
                    new Pose3d(
                            new Vector3d(poseTag.getDouble("px"), poseTag.getDouble("py"), poseTag.getDouble("pz")),
                            new Quaterniond(
                                    poseTag.getDouble("ox"),
                                    poseTag.getDouble("oy"),
                                    poseTag.getDouble("oz"),
                                    poseTag.getDouble("ow")),
                            new Vector3d(poseTag.getDouble("rx"), poseTag.getDouble("ry"), poseTag.getDouble("rz")),
                            new Vector3d(poseTag.getDouble("sx"), poseTag.getDouble("sy"), poseTag.getDouble("sz"))));
        }
    }
}
