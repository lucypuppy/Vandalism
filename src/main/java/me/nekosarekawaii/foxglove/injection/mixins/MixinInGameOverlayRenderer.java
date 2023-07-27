package me.nekosarekawaii.foxglove.injection.mixins;

import me.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public abstract class MixinInGameOverlayRenderer {

    @ModifyConstant(method = "renderFireOverlay", constant = @Constant(floatValue = -0.3F))
    private static float getFireOffset(final float value) {
        return value - Foxglove.getInstance().getConfigManager().getMainConfig().fireOverlayOffset.getValue();
    }

    @Inject(method = "renderUnderwaterOverlay", at = @At("HEAD"), cancellable = true)
    private static void injectRenderUnderwaterOverlay(final MinecraftClient minecraftClient, final MatrixStack matrixStack, final CallbackInfo ci) {
        if (!Foxglove.getInstance().getConfigManager().getMainConfig().waterOverlay.getValue()) ci.cancel();
    }

    @Inject(method = "renderInWallOverlay", at = @At("HEAD"), cancellable = true)
    private static void injectRenderUnderwaterOverlay(final Sprite sprite, final MatrixStack matrices, final CallbackInfo ci) {
        if (!Foxglove.getInstance().getConfigManager().getMainConfig().inWallOverlay.getValue()) ci.cancel();
    }

}
