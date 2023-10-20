package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.impl.main.VisualCategory;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignBlockEntityRenderer.class)
public abstract class MixinSignBlockEntityRenderer {

    @Inject(method = "renderText", at = @At(value = "HEAD"), cancellable = true)
    private void redirectRenderText(final BlockPos pos, final SignText signText, final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final int light, final int lineHeight, final int lineWidth, final boolean front, final CallbackInfo ci) {
        if (Vandalism.getInstance().getConfigManager().getMainConfig().visualCategory.hideSignText.getValue()) {
            for (int i = 0; i < 4; i++) {
                if (signText.getMessage(i, false).getString().equals(VisualCategory.SIGN_HIDE_SECRET)) {
                    ci.cancel();
                }
            }
        }
    }

}
