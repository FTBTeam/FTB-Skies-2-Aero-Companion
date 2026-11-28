package dev.ftb.mods.ftbskies2aerocompanion.compat.sable;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class PlotDestinations {

    private static final double SHIP_LANDING_LIFT = 0.5;

    private PlotDestinations() {}

    public static boolean isPlotSpace(Level level, Vec3 pos) {
        SubLevelContainer container = SubLevelContainer.getContainer(level);
        return container != null && container.inBounds(Mth.floor(pos.x) >> 4, Mth.floor(pos.z) >> 4);
    }

    public static Vec3 toWorld(Level level, Vec3 pos) {
        return Sable.HELPER.projectOutOfSubLevel(level, pos);
    }

    public static boolean isOnShip(Level level, Vec3 pos) {
        return Sable.HELPER.getContaining(level, (Position) pos) != null;
    }

    @Nullable
    public static Vec3 resolveTeleport(Level level, Vec3 pos) {
        if (!isPlotSpace(level, pos)) {
            return pos;
        }
        Vec3 world = resolve(level, pos);
        return world == null ? null : world.add(0.0, SHIP_LANDING_LIFT, 0.0);
    }

    public static DimensionTransition withPos(DimensionTransition transition, Vec3 pos) {
        if (pos.equals(transition.pos())) {
            return transition;
        }
        return new DimensionTransition(
                transition.newLevel(),
                pos,
                transition.speed(),
                transition.yRot(),
                transition.xRot(),
                transition.missingRespawnBlock(),
                transition.postDimensionTransition());
    }

    @Nullable
    public static Vec3 resolve(Level level, Vec3 pos) {
        SubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null) {
            return pos;
        }
        int chunkX = Mth.floor(pos.x) >> 4;
        int chunkZ = Mth.floor(pos.z) >> 4;
        if (!container.inBounds(chunkX, chunkZ)) {
            return pos;
        }
        SubLevel loaded = Sable.HELPER.getContaining(level, (Position) pos);
        if (loaded != null) {
            return loaded.logicalPose().transformPosition(pos);
        }
        if (container instanceof LastKnownPoseHolder holder) {
            int plotX = (chunkX >> container.getLogPlotSize()) - container.getOrigin().x;
            int plotZ = (chunkZ >> container.getLogPlotSize()) - container.getOrigin().y;
            Pose3dc pose = holder.ftbskies2aero$lastKnownPoses().get(container.getIndex(plotX, plotZ));
            if (pose != null) {
                return pose.transformPosition(pos);
            }
        }
        return null;
    }
}
