package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.util.rotation.rotationtypes.Rotation;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayer {

    @Redirect(method = {"sendMovementPackets", "tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
    private float redirectSendMovementPacketsAndTickYaw(final ClientPlayerEntity instance) {
        final Rotation rotation = Vandalism.getInstance().getRotationListener().getRotation();
        if (rotation != null) return rotation.getYaw();
        return instance.getYaw();
    }

    @Redirect(method = {"sendMovementPackets", "tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
    private float redirectSendMovementPacketsAndTickPitch(final ClientPlayerEntity instance) {
        final Rotation rotation = Vandalism.getInstance().getRotationListener().getRotation();
        if (rotation != null) return rotation.getPitch();
        return instance.getPitch();
    }

    @Redirect(method = "updateNausea", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;shouldPause()Z"))
    public boolean redirectUpdateNausea(final Screen screen) {
        return Vandalism.getInstance().getConfigManager().getMainConfig().visualCategory.portalScreen.getValue() || screen.shouldPause();
    }

}
