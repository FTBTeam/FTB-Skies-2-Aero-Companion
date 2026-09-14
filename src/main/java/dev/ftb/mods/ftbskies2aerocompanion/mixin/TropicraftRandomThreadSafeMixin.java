package dev.ftb.mods.ftbskies2aerocompanion.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class TropicraftRandomThreadSafeMixin {

    private static final ResourceLocation TROPICS =
            ResourceLocation.fromNamespaceAndPath("tropicraft", "tropics");

    @Shadow
    @Final
    @Mutable
    public RandomSource random;

    @SuppressWarnings("deprecation")
    @Inject(method = "<init>", at = @At("TAIL"))
    private void ftbskies2aero$threadSafeTropicraftRandom(CallbackInfo ci) {
        ServerLevel level = (ServerLevel) (Object) this;
        if (level.dimension().location().equals(TROPICS)) {
            this.random = RandomSource.createThreadSafe();
        }
    }
}
