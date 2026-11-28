package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.ironsspellbooks;

import dev.ftb.mods.ftbskies2aerocompanion.compat.sable.PlotDestinations;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.TraceParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MagicManager.class, remap = false)
public abstract class MagicManagerShipParticlesMixin {

    @Inject(
            method =
                    "spawnParticles(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDDZ)V",
            at = @At("HEAD"),
            cancellable = true)
    private static void ftbskies2aero$spawnShipParticlesInWorld(
            Level level,
            ParticleOptions options,
            double x,
            double y,
            double z,
            int count,
            double deltaX,
            double deltaY,
            double deltaZ,
            double speed,
            boolean force,
            CallbackInfo ci) {
        if (options instanceof TraceParticleOptions trace && trace.destination != null) {
            Vec3 destination = new Vec3(trace.destination);
            if (PlotDestinations.isOnShip(level, destination)) {
                Vec3 world = PlotDestinations.toWorld(level, destination);
                trace.destination.set((float) world.x, (float) world.y, (float) world.z);
            }
        }
        Vec3 origin = new Vec3(x, y, z);
        if (!PlotDestinations.isOnShip(level, origin)) {
            return;
        }
        Vec3 world = PlotDestinations.toWorld(level, origin);
        MagicManager.spawnParticles(
                level, options, world.x, world.y, world.z, count, deltaX, deltaY, deltaZ, speed, force);
        ci.cancel();
    }
}
