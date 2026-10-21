package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.tropicraft;

import dev.ftb.mods.ftbskies2aerocompanion.FTBSkies2AeroCompanion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(targets = "net.tropicraft.core.common.block.TropicraftTreeGrowers", remap = false)
public abstract class TropicraftTreeGrowersMahoganyMixin {

    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private static void ftbskies2aero$mahoganyWithoutPalms(String id, ResourceKey<ConfiguredFeature<?, ?>> featureKey,
                                                           CallbackInfoReturnable<TreeGrower> cir) {
        if ("rainforest".equals(id)) {
            ResourceKey<ConfiguredFeature<?, ?>> mahoganyTree =
                    ResourceKey.create(Registries.CONFIGURED_FEATURE, FTBSkies2AeroCompanion.id("mahogany_tree"));
            cir.setReturnValue(new TreeGrower("tropicraft:" + id, Optional.empty(), Optional.of(mahoganyTree), Optional.empty()));
        }
    }
}
