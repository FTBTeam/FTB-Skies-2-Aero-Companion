package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.oritech;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "rearth.oritech.client.init.ParticleContent", remap = false)
public abstract class ParticleContentStaggeredLineMixin {

    @WrapOperation(
            method = "lambda$spawnLineStaggered$0",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V")
    )
    private static void ftbskies2aero$addParticleOnRenderThread(Level level, ParticleOptions particle, double x, double y, double z,
                                                                double xSpeed, double ySpeed, double zSpeed, Operation<Void> original) {
        Minecraft.getInstance().execute(() -> original.call(level, particle, x, y, z, xSpeed, ySpeed, zSpeed));
    }
}
