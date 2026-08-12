package dev.ftb.mods.ftbskies2aerocompanion.ship;

import dev.ryanhcode.sable.api.sublevel.SubLevelObserver;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ShipHomeSubLevelObserver implements SubLevelObserver {
    private static final Logger LOGGER = LoggerFactory.getLogger("ShipHome");

    private final ServerLevel level;

    public ShipHomeSubLevelObserver(ServerLevel level) {
        this.level = level;
    }

    @Override
    public void onSubLevelAdded(SubLevel subLevel) {
        MinecraftServer server = level.getServer();
        if (server == null) return;
        try {
            ShipBindings.onSubLevelAssembled(server, subLevel);
        } catch (Throwable t) {
            LOGGER.error("Failed to rebind ship-home bindings for new sub-level", t);
        }
    }
}
