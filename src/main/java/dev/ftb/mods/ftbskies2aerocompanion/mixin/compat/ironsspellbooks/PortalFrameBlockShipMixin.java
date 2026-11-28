package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.ironsspellbooks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftbskies2aerocompanion.compat.sable.PlotDestinations;
import io.redspace.ironsspellbooks.block.portal_frame.PortalFrameBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PortalFrameBlock.class, remap = false)
public abstract class PortalFrameBlockShipMixin {

    @WrapOperation(
            method = "entityInside",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/phys/shapes/VoxelShape;move(DDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape ftbskies2aero$moveShapeToWorld(
            VoxelShape shape,
            double x,
            double y,
            double z,
            Operation<VoxelShape> original,
            @Local(argsOnly = true) Level level,
            @Local(argsOnly = true) Entity entity) {
        if (PlotDestinations.isOnShip(level, entity.position())) {
            return original.call(shape, x, y, z);
        }
        Vec3 world = PlotDestinations.toWorld(level, new Vec3(x, y, z));
        return original.call(shape, world.x, world.y, world.z);
    }
}
