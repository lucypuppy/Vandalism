package de.vandalismdevelopment.vandalism.injection.mixins.util.rotation;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.rotation.Rotation;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {

    @Redirect(method = {"sendMovementPackets", "tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
    private float vandalism$modifyRotationYaw(final ClientPlayerEntity instance) {
        final Rotation rotation = Vandalism.getInstance().getRotationListener().getRotation();
        if (rotation != null) return rotation.getYaw();
        return instance.getYaw();
    }

    @Redirect(method = {"sendMovementPackets", "tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
    private float vandalism$modifyRotationPitch(final ClientPlayerEntity instance) {
        final Rotation rotation = Vandalism.getInstance().getRotationListener().getRotation();
        if (rotation != null) return rotation.getPitch();
        return instance.getPitch();
    }

}
