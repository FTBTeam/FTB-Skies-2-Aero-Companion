package dev.ftb.mods.ftbskies2aerocompanion.mixin.compat.sophisticatedcore;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = {
        "net.p3pp3rf1y.sophisticatedbackpacks.network.BackpackContentsPayload",
        "net.p3pp3rf1y.sophisticatedstorage.network.StorageContentsPayload",
        "net.p3pp3rf1y.sophisticatedcore.compat.create.MountedStorageContentsPayload",
        "net.p3pp3rf1y.sophisticatedstorageinmotion.network.MovingStorageContentsPayload"
}, remap = false)
public abstract class ContentsPayloadSizeMixin {

    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETSTATIC,
                    target = "Lnet/minecraft/network/codec/ByteBufCodecs;COMPOUND_TAG:Lnet/minecraft/network/codec/StreamCodec;"
            ),
            require = 0
    )
    private static StreamCodec<?, ?> ftbskies2aero$readWithoutSizeLimit(StreamCodec<?, ?> original) {
        return ByteBufCodecs.TRUSTED_COMPOUND_TAG;
    }
}
