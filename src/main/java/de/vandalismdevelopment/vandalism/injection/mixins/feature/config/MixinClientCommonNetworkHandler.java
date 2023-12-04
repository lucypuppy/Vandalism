package de.vandalismdevelopment.vandalism.injection.mixins.feature.config;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui.minecraft.CustomRPConfirmScreen;
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
    private void vandalism$moreResourcePackOptions(final MinecraftClient instance, final Screen screen) {
        if (screen instanceof final ConfirmScreen confirmScreen) {
            if (Vandalism.getInstance().getConfigManager().getMainConfig().menuCategory.moreResourcePackOptions.getValue()) {
                instance.setScreen(new CustomRPConfirmScreen(confirmScreen.callback, confirmScreen.getTitle(), confirmScreen.message));
                return;
            }
        }
        instance.setScreen(screen);
    }

}
