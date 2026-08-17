package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.compactmachines;

import dev.ftb.mods.ftbskies2aerocompanion.compat.sable.PlotSpace;
import dev.ftb.mods.ftbskies2aerocompanion.ship.ShipBindings;
import dev.ftb.mods.ftbskies2aerocompanion.ship.ShipHomeData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.compactmods.machines.room.RoomHelper", remap = false)
public abstract class RoomHelperCaptureMixin {

    @Unique
    private static final Logger ftbskies2aero$LOGGER = LoggerFactory.getLogger("FTBSkies2Aero/CompactMachineReturn");

    @Inject(method = "teleportPlayerIntoRoom", at = @At("HEAD"))
    private static void ftbskies2aero$captureShipReturn(MinecraftServer server, ServerPlayer player,
                                                        @Coerce Object roomInstance, @Coerce Object entryPoint,
                                                        CallbackInfoReturnable<?> cir) {
        if (server == null) {
            return;
        }
        if (PlotSpace.isPlotSpace(player.serverLevel(), player.position())) {
            ftbskies2aero$LOGGER.warn("[capture] {} entered a room from inside sub-level plot space at {} in {}; Compact Machines will record that as their exit point",
                    player.getGameProfile().getName(), player.position(), player.serverLevel().dimension().location());
        }
        ShipHomeData data = ShipHomeData.get(server);
        ShipBindings.captureForPlayer(player, player.position(), player.getYRot(), player.getXRot())
                .ifPresentOrElse(binding -> {
                    data.setCompactReturn(player.getUUID(), binding);
                    ftbskies2aero$LOGGER.debug("[capture] {} entered a room from sub-level {} in {}, entry pos {}",
                            player.getGameProfile().getName(), binding.shipUuid(),
                            binding.shipDimension().location(), player.position());
                }, () -> ftbskies2aero$LOGGER.debug("[capture] {} entered a room from {} in {}, not on a sub-level, no binding stored",
                        player.getGameProfile().getName(), player.position(),
                        player.serverLevel().dimension().location()));
    }
}
