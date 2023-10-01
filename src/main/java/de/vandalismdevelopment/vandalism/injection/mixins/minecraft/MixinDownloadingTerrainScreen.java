package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import de.vandalismdevelopment.vandalism.util.ServerUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DownloadingTerrainScreen.class)
public abstract class MixinDownloadingTerrainScreen extends Screen {

    @Shadow
    public abstract void close();

    protected MixinDownloadingTerrainScreen(final Text ignored) {
        super(ignored);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void injectRender(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        context.drawCenteredTextWithShadow(this.textRenderer, "Press [ESC] to cancel.", this.width / 2, this.height / 2 - 50 + this.textRenderer.fontHeight, 0xFFFFFF);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            ServerUtils.disconnect();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

}
