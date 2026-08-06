package dev.ftb.mods.ftbskies2aerocompanion.compat.compactcrafting;

import net.minecraft.core.BlockPos;

public interface NearbyFieldLookup {
    int MAX_FIELD_REACH = 5;

    boolean ftbskies2aero$anyFieldNear(BlockPos pos);
}
