package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.simulated;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "dev.simulated_team.simulated.content.blocks.portable_engine.PortableEngineBlockEntity", remap = false)
public abstract class PortableEngineGoggleFuelNameMixin {

    @WrapOperation(method = "addToGoggleTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getHoverName()Lnet/minecraft/network/chat/Component;"))
    private Component ftbskies2aero$copyFuelName(ItemStack stack, Operation<Component> original) {
        return original.call(stack).copy();
    }
}
