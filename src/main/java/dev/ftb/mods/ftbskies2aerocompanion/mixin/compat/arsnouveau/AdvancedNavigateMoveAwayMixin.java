package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.arsnouveau;

import com.hollingsworth.arsnouveau.common.entity.pathfinding.AbstractAdvancedPathNavigate;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "com.hollingsworth.arsnouveau.common.entity.pathfinding.MinecoloniesAdvancedPathNavigate", remap = false)
public abstract class AdvancedNavigateMoveAwayMixin {

    @ModifyVariable(method = "moveAwayFromXYZ", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private BlockPos ftbskies2aero$localiseAvoidPos(BlockPos avoidPos) {
        if (avoidPos == null || !(((Object) this) instanceof AbstractAdvancedPathNavigate navigate)) {
            return avoidPos;
        }
        try {
            Mob mob = navigate.getOurEntity();
            if (mob == null) {
                return avoidPos;
            }
            SubLevel subLevel = Sable.HELPER.getTrackingSubLevel(mob);
            if (subLevel == null || Sable.HELPER.getContaining(mob.level(), avoidPos) == subLevel) {
                return avoidPos;
            }
            Vec3 local = subLevel.logicalPose().transformPositionInverse(Vec3.atCenterOf(avoidPos));
            return BlockPos.containing(local);
        } catch (Throwable ignored) {
            return avoidPos;
        }
    }
}
