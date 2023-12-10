package de.vandalismdevelopment.vandalism.injection.mixins.feature.config;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity implements MinecraftWrapper {

    @Inject(method = "isCreativeLevelTwoOp", at = @At("RETURN"), cancellable = true)
    private void vandalism$spoofIsCreativeLevelTwoOp(final CallbackInfoReturnable<Boolean> cir) {
        if (this.mc.player == ((PlayerEntity) (Object) this)) {
            if (Vandalism.getInstance().getClientSettings().getNetworkingSettings().spoofIsCreativeLevelTwoOp.getValue()) {
                cir.setReturnValue(true);
            }
        }
    }

}
