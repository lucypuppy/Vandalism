package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import de.vandalismdevelopment.vandalism.Vandalism;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity {

    @Inject(method = "isCreativeLevelTwoOp", at = @At("RETURN"), cancellable = true)
    private void injectIsCreativeLevelTwoOp(final CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().player == ((PlayerEntity) (Object) this)) {
            if (Vandalism.getInstance().getConfigManager().getMainConfig().accessibilityCategory.spoofIsCreativeLevelTwoOp.getValue()) {
                cir.setReturnValue(true);
            }
        }
    }

    @Redirect(method = "tickNewAi", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F"))
    private float redirectTickNewAi(final PlayerEntity instance) {
        if (MinecraftClient.getInstance().player == ((PlayerEntity) (Object) this)) {
            if (Vandalism.getInstance().getRotationListener().getRotation() != null) {
                return Vandalism.getInstance().getRotationListener().getRotation().getYaw();
            }
        }
        return instance.getYaw();
    }

}
