package dev.ftb.mods.ftbskies2aerocompanion.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
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
        Level level = (Level) (Object) this;
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ResourceKey<Level> dimension = level.dimension();
        if (dimension != null && TROPICS.equals(dimension.location())) {
            this.random = RandomSource.createThreadSafe();
        }
    }
}
