package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.ironsspellbooks;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftbskies2aerocompanion.compat.sable.PlotDestinations;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Utils.class, remap = false)
public abstract class SpellUtilsShipMixin {

    @ModifyVariable(method = "handleSpellTeleport", at = @At("HEAD"), argsOnly = true)
    private static Vec3 ftbskies2aero$teleportOutOfPlotSpace(Vec3 destination, @Local(argsOnly = true) Entity entity) {
        return PlotDestinations.toWorld(entity.level(), destination);
    }

    @Inject(method = "raycastForBlock", at = @At("RETURN"), cancellable = true)
    private static void ftbskies2aero$hitLocationInCasterSpace(
            Level level, Vec3 start, Vec3 end, ClipContext.Fluid fluid, CallbackInfoReturnable<BlockHitResult> cir) {
        BlockHitResult hit = cir.getReturnValue();
        Vec3 location = hit.getLocation();
        SubLevel startSubLevel = Sable.HELPER.getContaining(level, (Position) start);
        if (startSubLevel == Sable.HELPER.getContaining(level, (Position) location)) {
            return;
        }
        Vec3 world = PlotDestinations.toWorld(level, location);
        Vec3 adjusted =
                startSubLevel != null ? startSubLevel.logicalPose().transformPositionInverse(world) : world;
        cir.setReturnValue(new BlockHitResult(adjusted, hit.getDirection(), hit.getBlockPos(), hit.isInside()));
    }
}
