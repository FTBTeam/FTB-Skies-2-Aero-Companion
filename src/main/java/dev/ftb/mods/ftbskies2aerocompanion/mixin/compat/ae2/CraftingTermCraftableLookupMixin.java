package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.ae2;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.menu.me.common.GridInventoryEntry;
import appeng.menu.me.common.IClientRepo;
import appeng.menu.me.items.CraftingTermMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(value = CraftingTermMenu.class, remap = false)
public abstract class CraftingTermCraftableLookupMixin {

    @Unique
    private static final long ftbskies2aero$CACHE_NANOS = 50_000_000L;

    @Shadow
    public abstract IClientRepo getClientRepo();

    @Unique
    private Set<AEKey> ftbskies2aero$craftableKeys;

    @Unique
    private long ftbskies2aero$craftableBuiltAt;

    @Unique
    private int ftbskies2aero$craftableEntryCount = -1;

    @Inject(method = "isCraftable", at = @At("HEAD"), cancellable = true)
    private void ftbskies2aero$indexedCraftableLookup(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || stack.isEmpty()) {
            cir.setReturnValue(false);
            return;
        }
        IClientRepo repo;
        try {
            repo = getClientRepo();
        } catch (Throwable ignored) {
            return;
        }
        if (repo == null) {
            return;
        }
        Set<GridInventoryEntry> entries = repo.getAllEntries();
        if (entries == null) {
            return;
        }
        long now = System.nanoTime();
        if (ftbskies2aero$craftableKeys == null
                || entries.size() != ftbskies2aero$craftableEntryCount
                || now - ftbskies2aero$craftableBuiltAt > ftbskies2aero$CACHE_NANOS) {
            Set<AEKey> craftable = new HashSet<>();
            for (GridInventoryEntry entry : entries) {
                if (entry.isCraftable()) {
                    craftable.add(entry.getWhat());
                }
            }
            ftbskies2aero$craftableKeys = craftable;
            ftbskies2aero$craftableEntryCount = entries.size();
            ftbskies2aero$craftableBuiltAt = now;
        }
        AEItemKey key = AEItemKey.of(stack);
        cir.setReturnValue(key != null && ftbskies2aero$craftableKeys.contains(key));
    }
}
