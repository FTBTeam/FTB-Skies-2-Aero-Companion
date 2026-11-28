package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.ironsspellbooks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ftb.mods.ftbskies2aerocompanion.compat.sable.PlotDestinations;
import io.redspace.ironsspellbooks.block.portal_frame.PortalFrameBlockEntity;
import io.redspace.ironsspellbooks.capabilities.magic.PortalManager;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalData;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalPos;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.UUID;

@Mixin(value = PortalFrameBlockEntity.class, remap = false)
public abstract class PortalFrameBlockEntityShipMixin {

    @Inject(method = "serverTick", at = @At("HEAD"))
    private static void ftbskies2aero$trackFrameOnShip(
            Level level, BlockPos pos, BlockState state, PortalFrameBlockEntity frame, CallbackInfo ci) {
        if (!(level instanceof ServerLevel)) {
            return;
        }
        UUID uuid = frame.getUUID();
        if (uuid == null) {
            return;
        }
        PortalData portalData = PortalManager.INSTANCE.getPortalData(uuid);
        if (portalData == null) {
            return;
        }
        boolean primary = uuid.equals(portalData.portalEntityId1);
        PortalPos current = primary ? portalData.globalPos1 : portalData.globalPos2;
        Vec3 location = frame.getPortalLocation().add(0.0, 0.1, 0.0);
        boolean frameInPlot = PlotDestinations.isPlotSpace(level, location);
        boolean storedInPlot = current != null && PlotDestinations.isPlotSpace(level, current.pos());
        if (!frameInPlot && !storedInPlot) {
            return;
        }
        if (current != null
                && current.dimension().equals(level.dimension())
                && current.pos().distanceToSqr(location) < 1.0E-4) {
            return;
        }
        PortalPos updated = PortalPos.of(level.dimension(), location, current != null ? current.rotation() : 0.0F);
        if (primary) {
            portalData.globalPos1 = updated;
        } else {
            portalData.globalPos2 = updated;
        }
    }

    @WrapOperation(
            method = "lambda$teleport$1",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/entity/Entity;teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z"))
    private boolean ftbskies2aero$resolveShipTeleport(
            Entity entity,
            ServerLevel level,
            double x,
            double y,
            double z,
            Set<RelativeMovement> relative,
            float yRot,
            float xRot,
            Operation<Boolean> original) {
        Vec3 destination = PlotDestinations.resolveTeleport(level, new Vec3(x, y, z));
        if (destination == null) {
            return false;
        }
        return original.call(entity, level, destination.x, destination.y, destination.z, relative, yRot, xRot);
    }

    @WrapOperation(
            method = "lambda$teleport$1",
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
