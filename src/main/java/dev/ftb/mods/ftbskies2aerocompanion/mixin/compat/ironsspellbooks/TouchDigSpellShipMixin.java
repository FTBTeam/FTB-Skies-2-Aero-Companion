package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.ironsspellbooks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftbskies2aerocompanion.compat.sable.PlotDestinations;
import io.redspace.ironsspellbooks.spells.nature.TouchDigSpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TouchDigSpell.class, remap = false)
public abstract class TouchDigSpellShipMixin {

    @WrapOperation(
            method = "onCast",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/phys/BlockHitResult;getLocation()Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 ftbskies2aero$hitLocationToWorld(
            BlockHitResult hit,
            Operation<Vec3> original,
            @Local(argsOnly = true) Level level,
            @Local(argsOnly = true) LivingEntity caster) {
        return ftbskies2aero$toCasterSpace(level, caster, original.call(hit));
    }

    @WrapOperation(
            method = "onCast",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;subtract(DDD)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 ftbskies2aero$particleLocationToWorld(
            Vec3 vec,
            double x,
            double y,
            double z,
            Operation<Vec3> original,
            @Local(argsOnly = true) Level level,
            @Local(argsOnly = true) LivingEntity caster) {
        return ftbskies2aero$toCasterSpace(level, caster, original.call(vec, x, y, z));
    }

    private static Vec3 ftbskies2aero$toCasterSpace(Level level, LivingEntity caster, Vec3 pos) {
        if (PlotDestinations.isOnShip(level, caster.getEyePosition())) {
            return pos;
        }
        return PlotDestinations.toWorld(level, pos);
    }
}
