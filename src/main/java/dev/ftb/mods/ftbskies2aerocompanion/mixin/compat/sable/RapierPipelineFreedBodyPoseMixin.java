package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.sable;

import dev.ftb.mods.ftbskies2aerocompanion.compat.sable.ActiveBodyTracker;
import dev.ryanhcode.sable.api.physics.PhysicsPipelineBody;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.ryanhcode.sable.physics.impl.rapier.RapierPhysicsPipeline", remap = false)
public abstract class RapierPipelineFreedBodyPoseMixin implements ActiveBodyTracker {

    @Shadow @Final private Int2ObjectMap<ServerSubLevel> activeSubLevels;

    @Override
    public boolean ftbskies2aero$hasActiveBody(int runtimeId) {
        return this.activeSubLevels.containsKey(runtimeId);
    }

    @Unique
    private boolean ftbskies2aero$isFreed(ServerSubLevel subLevel) {
        return subLevel == null || !this.activeSubLevels.containsKey(subLevel.getRuntimeId());
    }

    @Unique
    private boolean ftbskies2aero$isFreedBody(PhysicsPipelineBody body) {
        return body instanceof ServerSubLevel subLevel && ftbskies2aero$isFreed(subLevel);
    }

    @Inject(method = "readPose", at = @At("HEAD"), cancellable = true)
    private void ftbskies2aero$skipFreedBody(ServerSubLevel subLevel, Pose3d destination, CallbackInfoReturnable<Pose3d> cir) {
        if (ftbskies2aero$isFreed(subLevel)) {
            cir.setReturnValue(destination);
        }
    }

    @Inject(method = "remove(Ldev/ryanhcode/sable/sublevel/ServerSubLevel;)V", at = @At("HEAD"), cancellable = true)
    private void ftbskies2aero$skipDoubleRemove(ServerSubLevel subLevel, CallbackInfo ci) {
        if (ftbskies2aero$isFreed(subLevel)) {
            ci.cancel();
        }
    }

    @Inject(method = "onStatsChanged", at = @At("HEAD"), cancellable = true)
    private void ftbskies2aero$skipFreedStats(ServerSubLevel subLevel, CallbackInfo ci) {
        if (ftbskies2aero$isFreed(subLevel)) {
            ci.cancel();
        }
    }

    @Inject(method = "wakeUp", at = @At("HEAD"), cancellable = true)
    private void ftbskies2aero$skipFreedWake(PhysicsPipelineBody body, CallbackInfo ci) {
        if (ftbskies2aero$isFreedBody(body)) {
            ci.cancel();
        }
    }

    @Inject(method = "teleport", at = @At("HEAD"), cancellable = true)
    private void ftbskies2aero$skipFreedTeleport(PhysicsPipelineBody body, Vector3dc position, Quaterniondc orientation, CallbackInfo ci) {
        if (ftbskies2aero$isFreedBody(body)) {
            ci.cancel();
        }
    }

    @Inject(method = "applyImpulse", at = @At("HEAD"), cancellable = true)
    private void ftbskies2aero$skipFreedImpulse(PhysicsPipelineBody body, Vector3dc position, Vector3dc force, CallbackInfo ci) {
        if (ftbskies2aero$isFreedBody(body)) {
            ci.cancel();
        }
    }

    @Inject(method = "applyLinearAndAngularImpulse", at = @At("HEAD"), cancellable = true)
    private void ftbskies2aero$skipFreedLinearAngularImpulse(PhysicsPipelineBody body, Vector3dc force, Vector3dc torque, boolean wakeUp, CallbackInfo ci) {
        if (ftbskies2aero$isFreedBody(body)) {
            ci.cancel();
        }
    }

    @Inject(method = "addLinearAndAngularVelocity", at = @At("HEAD"), cancellable = true)
    private void ftbskies2aero$skipFreedVelocityChange(PhysicsPipelineBody body, Vector3dc linearVelocity, Vector3dc angularVelocity, CallbackInfo ci) {
        if (ftbskies2aero$isFreedBody(body)) {
            ci.cancel();
        }
    }

    @Inject(method = "getLinearVelocity", at = @At("HEAD"), cancellable = true)
    private void ftbskies2aero$skipFreedLinearVelocity(PhysicsPipelineBody body, Vector3d dest, CallbackInfoReturnable<Vector3d> cir) {
        if (ftbskies2aero$isFreedBody(body)) {
            cir.setReturnValue(dest);
        }
    }

    @Inject(method = "getAngularVelocity", at = @At("HEAD"), cancellable = true)
    private void ftbskies2aero$skipFreedAngularVelocity(PhysicsPipelineBody body, Vector3d dest, CallbackInfoReturnable<Vector3d> cir) {
        if (ftbskies2aero$isFreedBody(body)) {
            cir.setReturnValue(dest);
        }
    }
}
