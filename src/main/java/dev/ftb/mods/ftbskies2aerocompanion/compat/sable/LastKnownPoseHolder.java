package dev.ftb.mods.ftbskies2aerocompanion.compat.sable;

import dev.ryanhcode.sable.companion.math.Pose3d;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public interface LastKnownPoseHolder {

    Int2ObjectMap<Pose3d> ftbskies2aero$lastKnownPoses();
}
