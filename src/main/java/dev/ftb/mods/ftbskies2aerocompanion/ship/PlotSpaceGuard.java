package dev.ftb.mods.ftbskies2aerocompanion.ship;

import dev.ftb.mods.ftbskies2aerocompanion.FTBSkies2AeroCompanion;
import dev.ftb.mods.ftbskies2aerocompanion.compat.sable.PlotSpace;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@EventBusSubscriber(modid = FTBSkies2AeroCompanion.MOD_ID)
public final class PlotSpaceGuard {

    private static final Logger LOGGER = LoggerFactory.getLogger("FTBSkies2Aero/PlotSpaceGuard");

    private PlotSpaceGuard() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            check(player, "dimension change " + event.getFrom().location() + " -> " + event.getTo().location());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            check(player, "respawn endConquered=" + event.isEndConquered());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            check(player, "login");
        }
    }

    private static void check(ServerPlayer player, String trigger) {
        ServerLevel level = player.serverLevel();
        Vec3 pos = player.position();
        if (!PlotSpace.isPlotSpace(level, pos)) {
            return;
        }
        if (player.isSpectator()) {
            LOGGER.info("[plot-guard] {} is inside sub-level plot space at {} in {} after {}, left alone (spectator)",
                    player.getGameProfile().getName(), pos, level.dimension().location(), trigger);
            return;
        }
        Optional<PlotSpace.Resolved> resolved = PlotSpace.resolve(level, pos);
        if (resolved.isEmpty()) {
            LOGGER.error("[plot-guard] {} ended up inside sub-level plot space at {} in {} after {}, and no sub-level owns that plot, so they were left there",
                    player.getGameProfile().getName(), pos, level.dimension().location(), trigger);
            return;
        }
        PlotSpace.Resolved target = resolved.get();
        Vec3 world = target.worldPos();
        LOGGER.warn("[plot-guard] {} ended up inside sub-level plot space at {} in {} after {}, moving to {} on sub-level {}",
                player.getGameProfile().getName(), pos, level.dimension().location(), trigger, world, target.subLevelId());
        player.teleportTo(level, world.x, world.y, world.z, player.getYRot(), player.getXRot());
    }
}
