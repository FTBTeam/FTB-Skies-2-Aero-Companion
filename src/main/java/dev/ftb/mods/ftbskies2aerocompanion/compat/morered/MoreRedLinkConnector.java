package dev.ftb.mods.ftbskies2aerocompanion.compat.morered;

import commoble.morered.api.MoreRedAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class MoreRedLinkConnector {

    private static final ResourceLocation REDSTONE_LINK = ResourceLocation.fromNamespaceAndPath("create", "redstone_link");

    private MoreRedLinkConnector() {}

    public static void register() {
        Block link = BuiltInRegistries.BLOCK.get(REDSTONE_LINK);
        if (link != Blocks.AIR) {
            MoreRedAPI.getWireConnectabilityRegistry().put(link, MoreRedLinkConnector::canConnectToWire);
        }
    }

    private static boolean canConnectToWire(BlockGetter level, BlockPos linkPos, BlockState linkState, BlockPos wirePos,
                                            BlockState wireState, Direction wireFace, Direction directionToWire) {
        return wireFace == linkState.getValue(DirectionalBlock.FACING).getOpposite()
                && directionToWire.getAxis() != wireFace.getAxis();
    }
}
