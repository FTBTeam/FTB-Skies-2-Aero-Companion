package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.extendedindustrialization;

import aztech.modern_industrialization.definition.FluidLike;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "net.swedz.extended_industrialization.machines.recipe.BreweryMachineRecipeType", remap = false)
public class BreweryBlazingEssenceMixin {

    private static final ResourceLocation MOLTEN_BLAZE_ID =
            ResourceLocation.fromNamespaceAndPath("productivemetalworks", "molten_blaze");

    @ModifyArg(
            method = "generate(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/item/crafting/Ingredient;Lnet/minecraft/world/item/crafting/Ingredient;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/crafting/RecipeHolder;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/swedz/tesseract/neoforge/compat/mi/recipe/MIMachineRecipeBuilder;addFluidInput(Laztech/modern_industrialization/definition/FluidLike;I)Laztech/modern_industrialization/machines/recipe/MIRecipeJson;"
            ),
            index = 0
    )
    private FluidLike ftbskies$brewWithMoltenBlaze(FluidLike original) {
        Fluid moltenBlaze = BuiltInRegistries.FLUID.get(MOLTEN_BLAZE_ID);
        if (moltenBlaze != null && moltenBlaze != Fluids.EMPTY) {
            return FluidLike.of(moltenBlaze);
        }
        return original;
    }
}
