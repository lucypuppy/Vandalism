package de.nekosarekawaii.foxglove.injection.mixins.minecraft;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntryListWidget.class)
public abstract class MixinEntryListWidget {

    @Shadow
    private boolean renderBackground;

    @Shadow
    @Final
    protected MinecraftClient client;

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void injectDrawTexture(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        if (client != null && client.player != null && client.world != null && !(client.currentScreen instanceof PackScreen)) {
            this.renderBackground = false;
        }
    }

}
