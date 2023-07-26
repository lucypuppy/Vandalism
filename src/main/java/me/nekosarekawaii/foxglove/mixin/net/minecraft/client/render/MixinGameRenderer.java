package me.nekosarekawaii.foxglove.mixin.net.minecraft.client.render;

import me.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "renderNausea", at = @At(value = "HEAD"), cancellable = true)
    public void renderNausea(final DrawContext context, final float distortionStrength, final CallbackInfo ci) {
        if (!Foxglove.getInstance().getConfigManager().getMainConfig().nauseaOverlay.getValue()) ci.cancel();
    }

    @Inject(method = "tiltViewWhenHurt", at = @At(value = "HEAD"), cancellable = true)
    public void tiltViewWhenHurt(final MatrixStack matrices, final float tickDelta, final CallbackInfo ci) {
        if (!Foxglove.getInstance().getConfigManager().getMainConfig().hurtCam.getValue()) ci.cancel();
    }

}
