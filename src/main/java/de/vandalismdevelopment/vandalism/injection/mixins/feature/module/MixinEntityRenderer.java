package de.vandalismdevelopment.vandalism.injection.mixins.feature.module;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.render.VisualThrottleModule;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity> {

    @Redirect(method = "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"))
    private int vandalism$visualThrottleModifyDisplayNameLength(final TextRenderer instance, Text text, final float x, final float y, final int color, final boolean shadow, final Matrix4f matrix, final VertexConsumerProvider vertexConsumers, final TextRenderer.TextLayerType layerType, final int backgroundColor, final int light) {
        final VisualThrottleModule visualThrottleModule = Vandalism.getInstance().getModuleRegistry().getVisualThrottleModule();
        if (visualThrottleModule.isEnabled() && visualThrottleModule.modifyDisplayNameLength.getValue()) {
            final String oldTextString = text.getString();
            final int oldLength = oldTextString.length(), maxLength = visualThrottleModule.maxDisplayNameLength.getValue();
            if (oldLength > maxLength) {
                text = Text.literal(oldTextString.substring(0, maxLength)).setStyle(text.getStyle());
            }
        }
        return instance.draw(text, x, y, color, shadow, matrix, vertexConsumers, layerType, backgroundColor, light);
    }

}
