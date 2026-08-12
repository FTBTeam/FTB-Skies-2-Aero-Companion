package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.sable;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.mixinterface.player_freezing.PlayerFreezeExtension;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = Player.class, priority = 1500)
public abstract class PlayerFreezeHoldMixin {

    @Unique
    private static final Logger ftbskies2aero$LOGGER = LoggerFactory.getLogger("ShipHome");

    @Unique
    private static final int ftbskies2aero$MAX_HOLD_TICKS = 600;

    @Unique
    private int ftbskies2aero$freezeHoldTicks;

    @Inject(method = "sable$tickStopFreezing", at = @At("HEAD"), cancellable = true, remap = false, require = 0, expect = 0)
    private void ftbskies2aero$holdFreezeUntilSubLevelLoads(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        UUID frozenTo = ((PlayerFreezeExtension) self).sable$getFrozenToSubLevel();
        if (frozenTo == null) {
            ftbskies2aero$freezeHoldTicks = 0;
            return;
        }

        SubLevelContainer container;
        try {
            container = SubLevelContainer.getContainer(self.level());
        } catch (Throwable t) {
            ftbskies2aero$freezeHoldTicks = 0;
            return;
        }

        if (container != null && container.getSubLevel(frozenTo) != null) {
            ftbskies2aero$freezeHoldTicks = 0;
            return;
        }

        if (++ftbskies2aero$freezeHoldTicks == 1) {
            ftbskies2aero$LOGGER.debug("[freezeHold] holding {} at sub-level {} until it loads",
                    self.getName().getString(), frozenTo);
        }
        if (ftbskies2aero$freezeHoldTicks <= ftbskies2aero$MAX_HOLD_TICKS) {
            ci.cancel();
        } else if (ftbskies2aero$freezeHoldTicks == ftbskies2aero$MAX_HOLD_TICKS + 1) {
            ftbskies2aero$LOGGER.warn("[freezeHold] sub-level {} did not load within {} ticks, releasing {}",
                    frozenTo, ftbskies2aero$MAX_HOLD_TICKS, self.getName().getString());
        }
    }
}
