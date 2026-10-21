package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.arssable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = {
        "com.hollingsworth.ars_sable.common.SublevelPosData",
        "com.hollingsworth.ars_sable.common.WarpSublevelTargetData"
}, remap = false)
public abstract class ArsSableGlobalDataOverworldMixin {

    @Redirect(
            method = "from",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;overworld()Lnet/minecraft/server/level/ServerLevel;",
                    remap = true
            )
    )
    private static ServerLevel ftbskies2aero$overworldDuringLevelInit(MinecraftServer server, ServerLevel level) {
        ServerLevel overworld = server.overworld();
        return overworld != null ? overworld : level;
    }
}
