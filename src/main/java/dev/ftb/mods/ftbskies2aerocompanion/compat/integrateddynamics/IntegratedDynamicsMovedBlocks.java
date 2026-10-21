package dev.ftb.mods.ftbskies2aerocompanion.compat.integrateddynamics;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Collects the destination positions of a bulk block relocation so the IntegratedDynamics
 * reform can run over the blocks that actually moved instead of over an axis-aligned box
 * guessed from the sub-level plot. A plot with no loaded chunk bounds reports
 * {@code BoundingBox3i.EMPTY}, which is {@code Integer.MAX_VALUE} to {@code Integer.MIN_VALUE}
 * on every axis; feeding that to a box walk allocates until the server dies
 * (FTBTeam/FTB-Modpack-Issues#13285).
 *
 * <p>Server thread only, and scoped to a single {@code disassembleSubLevel} call.
 */
public final class IntegratedDynamicsMovedBlocks {

    private static final int MAX_CAPTURE = 1 << 18;

    private static List<BlockPos> capture;

    private IntegratedDynamicsMovedBlocks() {
    }

    public static void begin() {
        capture = new ArrayList<>();
    }

    public static boolean isCapturing() {
        return capture != null;
    }

    public static void record(BlockPos pos) {
        List<BlockPos> current = capture;
        if (current != null && current.size() < MAX_CAPTURE) {
            current.add(pos.immutable());
        }
    }

    public static Set<BlockPos> endWithNeighbours() {
        List<BlockPos> current = capture;
        capture = null;
        if (current == null || current.isEmpty()) {
            return Set.of();
        }
        Set<BlockPos> positions = new LinkedHashSet<>(current.size() * 2);
        for (BlockPos pos : current) {
            positions.add(pos);
            for (Direction direction : Direction.values()) {
                positions.add(pos.relative(direction));
            }
        }
        return positions;
    }
}
