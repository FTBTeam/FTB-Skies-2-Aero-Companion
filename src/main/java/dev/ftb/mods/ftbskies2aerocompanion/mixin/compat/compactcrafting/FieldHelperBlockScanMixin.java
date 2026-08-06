package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.compactcrafting;

import dev.compactmods.crafting.data.CCAttachments;
import dev.compactmods.crafting.field.ActiveWorldFields;
import dev.compactmods.crafting.field.FieldHelper;
import dev.ftb.mods.ftbskies2aerocompanion.compat.compactcrafting.NearbyFieldLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FieldHelper.class, remap = false)
public abstract class FieldHelperBlockScanMixin {

    @Inject(method = "checkBlockPlacement", at = @At("HEAD"), cancellable = true)
    private static void ftbskies2aero$skipProjectorScan(Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!level.hasData(CCAttachments.ACTIVE_FIELDS)) {
            cir.setReturnValue(true);
            return;
        }

        ActiveWorldFields fields = level.getData(CCAttachments.ACTIVE_FIELDS);
        if (fields instanceof NearbyFieldLookup lookup && !lookup.ftbskies2aero$anyFieldNear(pos)) {
            cir.setReturnValue(true);
        }
    }
}
