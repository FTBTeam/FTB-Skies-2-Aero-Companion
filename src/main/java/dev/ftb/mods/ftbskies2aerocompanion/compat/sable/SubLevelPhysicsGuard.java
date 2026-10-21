package dev.ftb.mods.ftbskies2aerocompanion.compat.sable;

import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

public final class SubLevelPhysicsGuard {

    private SubLevelPhysicsGuard() {
    }

    public static boolean hasFreedBody(ServerLevel level, ChunkPos chunkPos, PhysicsPipeline pipeline) {
        if (level == null || chunkPos == null || !(pipeline instanceof ActiveBodyTracker tracker)) {
            return false;
        }
        try {
            ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
            if (container == null) {
                return false;
            }
            LevelPlot plot = container.getPlot(chunkPos);
            if (plot == null) {
                return false;
            }
            SubLevel subLevel = plot.getSubLevel();
            if (!(subLevel instanceof ServerSubLevel serverSubLevel)) {
                return false;
            }
            return !tracker.ftbskies2aero$hasActiveBody(serverSubLevel.getRuntimeId());
        } catch (Throwable ignored) {
            return false;
        }
    }
}
