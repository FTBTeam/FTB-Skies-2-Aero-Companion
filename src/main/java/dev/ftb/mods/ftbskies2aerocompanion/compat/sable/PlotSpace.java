package dev.ftb.mods.ftbskies2aerocompanion.compat.sable;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public final class PlotSpace {

    private static final Logger LOGGER = LoggerFactory.getLogger("FTBSkies2Aero/PlotSpace");

    public record Resolved(UUID subLevelId, Vec3 worldPos) {}

    private PlotSpace() {
    }

    public static boolean isPlotSpace(ServerLevel level, Vec3 pos) {
        if (level == null || pos == null) {
            return false;
        }
        try {
            SubLevelContainer container = SubLevelContainer.getContainer(level);
            return container != null && container.inBounds(BlockPos.containing(pos));
        } catch (Throwable t) {
            LOGGER.debug("[plot-space] inBounds check failed for {} in {}", pos, level.dimension().location(), t);
            return false;
        }
    }

    public static Optional<Resolved> resolve(ServerLevel level, Vec3 pos) {
        if (level == null || pos == null) {
            return Optional.empty();
        }
        try {
            SubLevel sub = Sable.HELPER.getContaining(level, (Position) pos);
            if (sub == null) {
                LOGGER.debug("[plot-space] {} in {} is inside plot bounds but no sub-level owns it",
                        pos, level.dimension().location());
                return Optional.empty();
            }
            Pose3dc pose = sub.logicalPose();
            if (pose == null) {
                LOGGER.debug("[plot-space] sub-level {} has no pose", sub.getUniqueId());
                return Optional.empty();
            }
            Vec3 world = pose.transformPosition(pos);
            LOGGER.debug("[plot-space] plot {} in {} belongs to sub-level {}, world {}",
                    pos, level.dimension().location(), sub.getUniqueId(), world);
            return Optional.of(new Resolved(sub.getUniqueId(), world));
        } catch (Throwable t) {
            LOGGER.warn("[plot-space] failed to resolve {} in {}", pos, level.dimension().location(), t);
            return Optional.empty();
        }
    }
}
