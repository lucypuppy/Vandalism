package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.util.minecraft.ServerUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProgressScreen.class)
public abstract class MixinProgressScreen extends Screen {

    @Unique
    private static final String vandalism$CANCEL_MESSAGE = "Press [ESC] to cancel.";

    protected MixinProgressScreen(final Text ignored) {
        super(ignored);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderEscapingText(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        if (Vandalism.getInstance().getClientSettings().getMenuSettings().progressScreenEscaping.getValue()) {
            context.drawCenteredTextWithShadow(this.textRenderer, vandalism$CANCEL_MESSAGE, this.width / 2, this.height / 2 - 50 + this.textRenderer.fontHeight, 0xFFFFFF);
        }
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (Vandalism.getInstance().getClientSettings().getMenuSettings().progressScreenEscaping.getValue()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                ServerUtil.disconnect();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

}
