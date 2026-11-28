package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.ironsspellbooks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ftb.mods.ftbskies2aerocompanion.compat.sable.PlotDestinations;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PortalEntity.class, remap = false)
public abstract class PortalEntityShipMixin {

    @WrapOperation(
            method = "lambda$checkForEntitiesToTeleport$1",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;teleportTo(DDD)V"))
    private void ftbskies2aero$resolveShipTeleport(
            Entity entity, double x, double y, double z, Operation<Void> original) {
        Vec3 destination = PlotDestinations.resolveTeleport(entity.level(), new Vec3(x, y, z));
        if (destination != null) {
            original.call(entity, destination.x, destination.y, destination.z);
        }
    }

    @WrapOperation(
            method = "lambda$checkForEntitiesToTeleport$1",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/entity/Entity;changeDimension(Lnet/minecraft/world/level/portal/DimensionTransition;)Lnet/minecraft/world/entity/Entity;"))
    private Entity ftbskies2aero$resolveShipDimensionChange(
            Entity entity, DimensionTransition transition, Operation<Entity> original) {
        Vec3 destination = PlotDestinations.resolveTeleport(transition.newLevel(), transition.pos());
        if (destination == null) {
            return null;
        }
        return original.call(entity, PlotDestinations.withPos(transition, destination));
    }
}
