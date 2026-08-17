package dev.ftb.mods.ftbskies2aerocompanion.compat.createaeronauticsftbchunks;

import dev.ftb.mods.ftbskies2aerocompanion.FTBSkies2AeroCompanion;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = FTBSkies2AeroCompanion.MOD_ID)
public final class ContraptionForceLoadShutdown {

    private static final String MOD_ID = "create_aeronautics_ftb_chunks";
    private static final String MANAGER_CLASS = "com.leclowndu93150.create_aeronautics_ftb_chunks.ContraptionForceLoadManager";

    private static Boolean available;
    private static Method getForceLoadedSubLevels;
    private static Method disablePhysicsForceLoad;

    private ContraptionForceLoadShutdown() {
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        if (!resolve()) {
            return;
        }
        MinecraftServer server = event.getServer();
        if (server == null) {
            return;
        }
        try {
            if (!(getForceLoadedSubLevels.invoke(null) instanceof Collection<?> forceLoaded) || forceLoaded.isEmpty()) {
                return;
            }
            List<UUID> subLevelIds = new ArrayList<>(forceLoaded.size());
            for (Object entry : forceLoaded) {
                if (entry instanceof UUID subLevelId) {
                    subLevelIds.add(subLevelId);
                }
            }
            for (UUID subLevelId : subLevelIds) {
                try {
                    disablePhysicsForceLoad.invoke(null, server, subLevelId);
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private static boolean resolve() {
        if (available == null) {
            available = Boolean.FALSE;
            if (ModList.get().isLoaded(MOD_ID)) {
                try {
                    Class<?> manager = Class.forName(MANAGER_CLASS);
                    getForceLoadedSubLevels = manager.getMethod("getPhysicsForceLoadedSubLevels");
                    disablePhysicsForceLoad = manager.getMethod("disablePhysicsForceLoad", MinecraftServer.class, UUID.class);
                    available = Boolean.TRUE;
                } catch (Throwable ignored) {
                }
            }
        }
        return available;
    }
}
