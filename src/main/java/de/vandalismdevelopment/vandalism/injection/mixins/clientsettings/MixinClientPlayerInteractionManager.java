package de.vandalismdevelopment.vandalism.injection.mixins.clientsettings;

import de.vandalismdevelopment.vandalism.Vandalism;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {

    @Inject(method = "hasLimitedAttackSpeed", at = @At("HEAD"), cancellable = true)
    public void vandalism$eliminateHitDelay(final CallbackInfoReturnable<Boolean> cir) {
        if (Vandalism.getInstance().getClientSettings().getNetworkingSettings().eliminateHitDelay.getValue()) {
            cir.setReturnValue(false);
        }
    }

}