package de.nekosarekawaii.foxglove.injection.mixins;

import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.util.MinecraftWrapper;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity implements MinecraftWrapper {

    @Inject(method = "isCreativeLevelTwoOp", at = @At("RETURN"), cancellable = true)
    private void injectIsCreativeLevelTwoOp(final CallbackInfoReturnable<Boolean> cir) {
        if (player() == (Object) this) {
            if (Foxglove.getInstance().getConfigManager().getMainConfig().spoofIsCreativeLevelTwoOp.getValue()) {
                cir.setReturnValue(true);
            }
        }
    }

    @Redirect(method = "tickNewAi", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F"))
    private float redirectTickNewAi(PlayerEntity instance) {
        if (player() == (Object) this) {
            if (Foxglove.getInstance().getRotationListener().getRotation() != null) {
                return Foxglove.getInstance().getRotationListener().getRotation().getYaw();
            }
        }

        return instance.getYaw();
    }

}
