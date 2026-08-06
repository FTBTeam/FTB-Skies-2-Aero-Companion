package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.compactcrafting;

import dev.compactmods.crafting.field.ActiveWorldFields;
import dev.ftb.mods.ftbskies2aerocompanion.compat.compactcrafting.NearbyFieldLookup;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;

@Mixin(value = ActiveWorldFields.class, remap = false)
public abstract class ActiveWorldFieldsNearbyMixin implements NearbyFieldLookup {

    @Shadow
    @Final
    private HashMap<BlockPos, ?> fields;

    @Override
    public boolean ftbskies2aero$anyFieldNear(BlockPos pos) {
        if (fields.isEmpty()) {
            return false;
        }

        for (BlockPos center : fields.keySet()) {
            if (Math.abs(center.getX() - pos.getX()) <= MAX_FIELD_REACH
                    && Math.abs(center.getY() - pos.getY()) <= MAX_FIELD_REACH
                    && Math.abs(center.getZ() - pos.getZ()) <= MAX_FIELD_REACH) {
                return true;
            }
        }

        return false;
    }
}
