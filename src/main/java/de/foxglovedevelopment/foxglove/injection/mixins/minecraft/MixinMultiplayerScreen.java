package de.foxglovedevelopment.foxglove.injection.mixins.minecraft;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen extends Screen {

    @Shadow
    private Screen parent;

    protected MixinMultiplayerScreen(final Text ignored) {
        super(ignored);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void injectTick(final CallbackInfo ci) {
        if (client == null) return;
        if (this.parent instanceof GameMenuScreen && client.player == null) {
            this.parent = new TitleScreen();
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void injectKeyPressed(final int keyCode, final int scanCode, final int modifiers, final CallbackInfoReturnable<Boolean> cir) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            cir.setReturnValue(false);
        }
    }

}
