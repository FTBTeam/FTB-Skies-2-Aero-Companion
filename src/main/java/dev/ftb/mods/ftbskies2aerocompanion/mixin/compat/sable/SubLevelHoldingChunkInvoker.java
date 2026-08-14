package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.sable;

import dev.ryanhcode.sable.sublevel.storage.HoldingSubLevel;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;
import java.util.UUID;

@Mixin(value = SubLevelHoldingChunk.class, remap = false)
public interface SubLevelHoldingChunkInvoker {

    @Invoker("snatch")
    Collection<HoldingSubLevel> ftbskies2aero$invokeSnatch(UUID subLevelId);
}
