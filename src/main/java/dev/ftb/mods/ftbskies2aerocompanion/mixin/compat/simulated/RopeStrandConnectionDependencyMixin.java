package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.simulated;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBlockEntity", remap = false)
public interface RopeStrandConnectionDependencyMixin {

    @WrapOperation(
            method = "sable$getConnectionDependencies",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/ryanhcode/sable/ActiveSableCompanion;getContaining(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/Position;)Ldev/ryanhcode/sable/sublevel/SubLevel;"
            )
    )
    private SubLevel ftbskies2aero$skipUnanchoredRopeEnd(ActiveSableCompanion companion, Level level, Position attachment, Operation<SubLevel> original) {
        return attachment == null ? null : original.call(companion, level, attachment);
    }
}
