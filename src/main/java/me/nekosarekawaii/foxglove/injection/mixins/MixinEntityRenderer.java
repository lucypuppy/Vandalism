package me.nekosarekawaii.foxglove.injection.mixins;

import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.exploit.ExploitFixerModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity> {

    @Shadow
    @Final
    protected EntityRenderDispatcher dispatcher;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(
            method = "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injectRenderLabelIfPresent(final T entity, Text text, final MatrixStack matrixStack, final VertexConsumerProvider vertexConsumerProvider, final int light, final CallbackInfo ci) {
        final ExploitFixerModule exploitFixerModule = Foxglove.getInstance().getModuleRegistry().getExploitFixerModule();
        if (exploitFixerModule.isEnabled() && exploitFixerModule.modifyDisplayNameLength.getValue()) {
            final String oldTextString = text.getString();
            final int oldLength = oldTextString.length(), maxLength = exploitFixerModule.maxDisplayNameLength.getValue();
            if (oldLength > maxLength) {
                text = Text.literal(oldTextString.substring(0, maxLength)).setStyle(text.getStyle());
                final double d = this.dispatcher.getSquaredDistanceToCamera(entity);
                if (d > 4096.0) return;
                final boolean notSneaking = !entity.isSneaky();
                final TextRenderer textRenderer = this.getTextRenderer();
                final float
                        labelHeight = entity.getNameLabelHeight(),
                        labelY = "deadmau5".equals(text.getString()) ? -10f : 0f,
                        bgOpacity = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f),
                        labelX = -textRenderer.getWidth(text) / 2f;
                matrixStack.push();
                matrixStack.translate(0.0f, labelHeight, 0.0f);
                matrixStack.multiply(this.dispatcher.getRotation());
                matrixStack.scale(-0.025f, -0.025f, 0.025f);
                final Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
                textRenderer.draw(text, labelX, labelY, 0x20FFFFFF, false, matrix4f, vertexConsumerProvider, notSneaking ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, (int) (bgOpacity * 255.0f) << 24, light);
                if (notSneaking)
                    textRenderer.draw(text, labelX, labelY, -1, false, matrix4f, vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, light);
                matrixStack.pop();
                ci.cancel();
            }
        }
    }

}
