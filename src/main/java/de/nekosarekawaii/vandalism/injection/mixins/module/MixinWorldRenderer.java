package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.render.ESPModule;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;setColor(IIII)V"))
    private void hookEsp(final OutlineVertexConsumerProvider instance, int red, int green, int blue, int alpha) {
        final ESPModule espModule = Vandalism.getInstance().getModuleManager().getEspModule();
        if (espModule.isActive()) {
            final var color = espModule.outlineColor.getValue();

            red = color.getColor().getRed();
            green = color.getColor().getGreen();
            blue = color.getColor().getBlue();
            alpha = color.getColor().getAlpha();

        }
        instance.setColor(red, green, blue, alpha);
    }

}
