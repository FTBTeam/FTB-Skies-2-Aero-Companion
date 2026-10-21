package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.sable;

import dev.ftb.mods.ftbskies2aerocompanion.compat.sable.SubLevelPhysicsGuard;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SubLevelPhysicsSystem.class, remap = false)
public abstract class SubLevelPhysicsBlockChangeMixin {

    @Shadow
    public abstract ServerLevel getLevel();

    @Shadow
    public abstract PhysicsPipeline getPipeline();

    @Inject(method = "handleBlockChange", at = @At("HEAD"), cancellable = true)
    private void ftbskies2aero$skipFreedBody(SectionPos sectionPos, LevelChunkSection section, int localX, int localY, int localZ, BlockState oldState, BlockState newState, CallbackInfo ci) {
        if (SubLevelPhysicsGuard.hasFreedBody(this.getLevel(), sectionPos.chunk(), this.getPipeline())) {
            ci.cancel();
        }
    }
}
