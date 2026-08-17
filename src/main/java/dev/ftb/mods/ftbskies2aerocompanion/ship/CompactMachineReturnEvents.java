package dev.ftb.mods.ftbskies2aerocompanion.ship;

import dev.ftb.mods.ftbskies2aerocompanion.FTBSkies2AeroCompanion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@EventBusSubscriber(modid = FTBSkies2AeroCompanion.MOD_ID)
public final class CompactMachineReturnEvents {

    private static final Logger LOGGER = LoggerFactory.getLogger("FTBSkies2Aero/CompactMachineReturn");

    private CompactMachineReturnEvents() {
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }
        ShipHomeData data = ShipHomeData.get(server);
        Optional<ShipBinding> binding = data.getCompactReturn(player.getUUID());
        if (binding.isEmpty()) {
            return;
        }
        if (!event.getTo().equals(binding.get().shipDimension())) {
            LOGGER.debug("[return] {} left the compact dimension into {} but their binding is for {}, leaving them where Compact Machines put them ({})",
                    player.getGameProfile().getName(), event.getTo().location(),
                    binding.get().shipDimension().location(), player.position());
            return;
        }
        data.clearCompactReturn(player.getUUID());
        Optional<ShipBindings.Resolved> resolved = ShipBindings.resolveAnchor(server, binding.get());
        if (resolved.isEmpty()) {
            LOGGER.warn("[return] {} left a room bound to sub-level {}, which could not be resolved, leaving them at {} in {}",
                    player.getGameProfile().getName(), binding.get().shipUuid(),
                    player.position(), event.getTo().location());
            return;
        }
        ServerLevel level = server.getLevel(binding.get().shipDimension());
        if (level == null) {
            LOGGER.warn("[return] dimension {} is not loaded, leaving {} at {}",
                    binding.get().shipDimension().location(), player.getGameProfile().getName(), player.position());
            return;
        }
        ShipBindings.Resolved r = resolved.get();
        Vec3 pos = r.worldPos();
        LOGGER.debug("[return] {} left a room bound to sub-level {}, moving from {} to {} yaw={}",
                player.getGameProfile().getName(), binding.get().shipUuid(), player.position(), pos, r.yaw());
        player.teleportTo(level, pos.x, pos.y, pos.z, r.yaw(), r.pitch());
    }
}
