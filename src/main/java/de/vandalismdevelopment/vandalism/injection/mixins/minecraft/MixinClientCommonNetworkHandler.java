package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui.screen.CustomResourcePackConfirmScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientCommonNetworkHandler.class)
public abstract class MixinClientCommonNetworkHandler {

    @Redirect(method = "showPackConfirmationScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    private void redirectShowPackConfirmationScreen(final MinecraftClient instance, final Screen screen) {
        if (screen instanceof final ConfirmScreen confirmScreen) {
            if (Vandalism.getInstance().getConfigManager().getMainConfig().moreResourcePackOptions.getValue()) {
                instance.setScreen(new CustomResourcePackConfirmScreen(confirmScreen.callback, confirmScreen.getTitle(), confirmScreen.message));
                return;
            }
        }
        instance.setScreen(screen);
    }

}
