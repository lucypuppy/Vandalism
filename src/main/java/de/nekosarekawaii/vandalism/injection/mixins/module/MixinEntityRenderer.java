package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.render.VisualThrottleModule;
import de.nekosarekawaii.vandalism.util.minecraft.TextUtil;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @Unique
    private Text vandalism$modifyDisplayNameLength(final Text text) {
        final VisualThrottleModule visualThrottleModule = Vandalism.getInstance().getModuleManager().getVisualThrottleModule();
        if (visualThrottleModule.isActive() && visualThrottleModule.modifyDisplayNameLength.getValue()) {
            return TextUtil.trimText(text, visualThrottleModule.maxDisplayNameLength.getValue());
        }
        return text;
    }

    @Redirect(method = "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z", shift = At.Shift.BEFORE))
    private String hookVisualThrottle(final Text text) {
        return this.vandalism$modifyDisplayNameLength(text).getString();
    }

    @ModifyArgs(method = "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Lnet/minecraft/text/StringVisitable;)I"))
    private void hookVisualThrottle_Width(final Args args) {
        args.set(0, this.vandalism$modifyDisplayNameLength(args.get(0)));
    }

    @ModifyArgs(method = "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I", ordinal = 0))
    private void hookVisualThrottle_Text1(final Args args) {
        args.set(0, this.vandalism$modifyDisplayNameLength(args.get(0)));
    }

    @ModifyArgs(method = "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I", ordinal = 1))
    private void hookVisualThrottle_Text2(final Args args) {
        args.set(0, this.vandalism$modifyDisplayNameLength(args.get(0)));
    }

}
