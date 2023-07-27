package me.nekosarekawaii.foxglove.injection.mixins;

import me.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Inject(method = "hasLimitedAttackSpeed", at = @At("HEAD"), cancellable = true)
    public void removeDelay(CallbackInfoReturnable<Boolean> cir) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().eliminateHitDelay.getValue()) {
            cir.setReturnValue(false);
        }
    }
}
