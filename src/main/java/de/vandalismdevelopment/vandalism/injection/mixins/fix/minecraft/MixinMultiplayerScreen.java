package de.vandalismdevelopment.vandalism.injection.mixins.fix.minecraft;

import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen extends Screen implements MinecraftWrapper {

    @Shadow
    private Screen parent;

    protected MixinMultiplayerScreen(final Text ignored) {
        super(ignored);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void vandalism$fixInvalidParentScreen(final CallbackInfo ci) {
        if (this.parent instanceof GameMenuScreen && this.mc.player == null) {
            this.parent = new TitleScreen();
        }
    }

}
