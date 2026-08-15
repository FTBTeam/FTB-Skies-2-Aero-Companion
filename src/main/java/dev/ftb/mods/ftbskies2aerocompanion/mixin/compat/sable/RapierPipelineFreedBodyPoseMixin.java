package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.sable;

import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.ryanhcode.sable.physics.impl.rapier.RapierPhysicsPipeline", remap = false)
public abstract class RapierPipelineFreedBodyPoseMixin {

    @Shadow @Final private Int2ObjectMap<ServerSubLevel> activeSubLevels;

    @Inject(method = "readPose", at = @At("HEAD"), cancellable = true)
    private void ftbskies2aero$skipFreedBody(ServerSubLevel subLevel, Pose3d destination, CallbackInfoReturnable<Pose3d> cir) {
        if (subLevel == null || !this.activeSubLevels.containsKey(subLevel.getRuntimeId())) {
            cir.setReturnValue(destination);
        }
    }
}
