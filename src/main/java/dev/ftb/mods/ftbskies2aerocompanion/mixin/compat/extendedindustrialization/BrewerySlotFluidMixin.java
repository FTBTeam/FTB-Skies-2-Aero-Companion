package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.extendedindustrialization;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Supplier;

@Mixin(targets = "net.swedz.extended_industrialization.EIMachines", remap = false)
public class BrewerySlotFluidMixin {

    private static final ResourceLocation MOLTEN_BLAZE_ID =
            ResourceLocation.fromNamespaceAndPath("productivemetalworks", "molten_blaze");

    @ModifyArg(
            method = "lambda$singleBlockCrafting$37(Lnet/swedz/tesseract/neoforge/compat/mi/machine/builder/slots/MachineSlotConfiguration$Builder;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/swedz/tesseract/neoforge/compat/mi/machine/builder/slots/MachineSlotConfiguration$Builder;fluidInput(IILjava/util/function/Supplier;I)Lnet/swedz/tesseract/neoforge/compat/mi/machine/builder/slots/MachineSlotConfiguration$Builder;"
            ),
            index = 2
    )
    private static Supplier<Fluid> ftbskies$brewerySlotMoltenBlaze(Supplier<Fluid> original) {
        return () -> {
            Fluid moltenBlaze = BuiltInRegistries.FLUID.get(MOLTEN_BLAZE_ID);
            if (moltenBlaze != null && moltenBlaze != Fluids.EMPTY) {
                return moltenBlaze;
            }
            return original.get();
        };
    }
}
