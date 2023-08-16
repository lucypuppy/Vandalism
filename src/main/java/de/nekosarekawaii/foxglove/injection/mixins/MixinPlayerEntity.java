package de.nekosarekawaii.foxglove.injection.mixins;

import de.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity {

    @Inject(method = "isCreativeLevelTwoOp", at = @At("RETURN"), cancellable = true)
    private void injectIsCreativeLevelTwoOp(final CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().player == ((PlayerEntity) (Object) this)) {
            if (Foxglove.getInstance().getConfigManager().getMainConfig().spoofIsCreativeLevelTwoOp.getValue()) {
                cir.setReturnValue(true);
            }
        }
    }

}
