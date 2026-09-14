package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.simulated;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.logging.LogUtils;
import dev.ftb.mods.ftbskies2aerocompanion.compat.integrateddynamics.IntegratedDynamicsMovedBlocks;
import dev.ftb.mods.ftbskies2aerocompanion.compat.integrateddynamics.IntegratedDynamicsNetworkReform;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import org.cyclops.integrateddynamics.core.helper.CableHelpers;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

/**
 * The disassembly counterpart to {@code SubLevelAssemblyHelperCableNetworkMixin}. Assembly
 * (parent to sub-level) is bracketed and reformed; disassembly (sub-level back to parent)
 * runs through {@code SimAssemblyHelper.disassembleSubLevel} and was left uncovered, so the
 * sub-level cables tore their network down / dropped parts on the way out and the cables
 * placed back into the parent came up network-dead. After a disassemble/reassemble cycle the
 * parts (e.g. a steering block reader) lose their bindings.
 *
 * <p>Bracket the whole disassembly with {@link CableHelpers#setRemovingCable(boolean)} so the
 * sub-level cable removal skips ID's teardown, then reform the network in the parent over the
 * blocks the disassembly actually relocated, plus their direct neighbours so cables already in
 * the world link up with the ones the ship brought down.
 */
@Mixin(targets = "dev.simulated_team.simulated.util.SimAssemblyHelper", remap = false)
public abstract class SimAssemblyHelperDisassembleMixin {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Inject(method = "disassembleSubLevel", at = @At("HEAD"))
    private static void ftbskies2aero$beginDisassembleCableMove(Level level, SubLevel subLevel, BlockPos worldPos, BlockPos assemblerPos,
                                                                Rotation rotation, boolean flag, CallbackInfo ci) {
        CableHelpers.setRemovingCable(true);
        IntegratedDynamicsMovedBlocks.begin();
    }

    @Inject(method = "disassembleSubLevel", at = @At("RETURN"))
    private static void ftbskies2aero$endDisassembleCableMove(Level level, SubLevel subLevel, BlockPos worldPos, BlockPos assemblerPos,
                                                              Rotation rotation, boolean flag, CallbackInfo ci,
                                                              @Local SubLevelAssemblyHelper.AssemblyTransform transform) {
        CableHelpers.setRemovingCable(false);
        Set<BlockPos> moved = IntegratedDynamicsMovedBlocks.endWithNeighbours();
        if (moved.isEmpty()) {
            return;
        }
        try {
            IntegratedDynamicsNetworkReform.reform(transform.getLevel(), moved);
        } catch (Throwable t) {
            LOGGER.error("Failed to reform IntegratedDynamics networks after sub-level disassembly", t);
        }
    }
}
