package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.render.ESPModule;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;setColor(IIII)V"))
    private void vandalism$espOutlineColor(final OutlineVertexConsumerProvider instance, final int red, final int green, final int blue, final int alpha) {
        final ESPModule espModule = Vandalism.getInstance().getModuleManager().getEspModule();
        if (espModule.isActive()) {
            final Color color = espModule.outlineColor.getValue();
            instance.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            return;
        }
        instance.setColor(red, green, blue, alpha);
    }

}