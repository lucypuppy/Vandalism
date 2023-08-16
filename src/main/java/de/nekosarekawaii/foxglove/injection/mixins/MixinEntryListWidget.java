package de.nekosarekawaii.foxglove.injection.mixins;

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
    @Final
    protected MinecraftClient client;

    @Shadow
    private boolean renderBackground;

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void injectDrawTexture(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        if (this.client != null && this.client.player != null && this.client.world != null && !(this.client.currentScreen instanceof PackScreen)) {
            this.renderBackground = false;
        }
    }

}
