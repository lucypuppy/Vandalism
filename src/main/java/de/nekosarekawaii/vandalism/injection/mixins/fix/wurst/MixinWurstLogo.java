package de.nekosarekawaii.vandalism.injection.mixins.fix.wurst;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.integration.hud.impl.WatermarkHUDElement;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.wurstclient.hud.WurstLogo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WurstLogo.class, remap = false)
public abstract class MixinWurstLogo {

    @Shadow
    protected abstract void drawQuads(final MatrixStack matrices, final int x1, final int y1, final int x2, final int y2, final float r, final float g, final float b, final float a);

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/wurstclient/hud/WurstLogo;drawQuads(Lnet/minecraft/client/util/math/MatrixStack;IIIIFFFF)V"))
    private void cancelWurstLogoQuad(final WurstLogo instance, final MatrixStack matrices, final int x1, final int y1, final int x2, final int y2, final float r, final float g, final float b, final float a) {
        final WatermarkHUDElement watermarkHUDElement = Vandalism.getInstance().getHudManager().watermarkHUDElement;
        this.drawQuads(matrices, watermarkHUDElement.x + 18, watermarkHUDElement.y + 34, watermarkHUDElement.x + 27, watermarkHUDElement.y + 44, r, g, b, a);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"))
    private int cancelWurstLogoDrawText(final DrawContext instance, final TextRenderer textRenderer, final String text, final int x, final int y, final int color, final boolean shadow) {
        final WatermarkHUDElement watermarkHUDElement = Vandalism.getInstance().getHudManager().watermarkHUDElement;
        return instance.drawText(textRenderer, "X", watermarkHUDElement.x + 20, watermarkHUDElement.y + 35, color, shadow);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V"))
    private void moveWurstLogoDrawTexture(final DrawContext instance, final Identifier texture, final int x, final int y, final float u, final float v, final int width, final int height, final int textureWidth, final int textureHeight) {
        final WatermarkHUDElement watermarkHUDElement = Vandalism.getInstance().getHudManager().watermarkHUDElement;
        instance.drawTexture(texture, watermarkHUDElement.x + 30, watermarkHUDElement.y + 30, u, v, width, height, textureWidth, textureHeight);

    }

    @Inject(method = "getVersionString", at = @At("RETURN"), cancellable = true)
    private void changeWurstLogoVersionString(final CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("");
    }

}
