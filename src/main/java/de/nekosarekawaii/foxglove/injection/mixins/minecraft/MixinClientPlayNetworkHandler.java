package de.nekosarekawaii.foxglove.injection.mixins.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.gui.screen.CustomResourcePackConfirmScreen;
import de.nekosarekawaii.foxglove.util.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(final String message, final CallbackInfo ci) {
        final String prefix = Foxglove.getInstance().getConfigManager().getMainConfig().commandPrefix.getValue();
        if (message.startsWith(prefix)) {
            try {
                Foxglove.getInstance().getCommandRegistry().commandDispatch(message.substring(prefix.length()));
            } catch (final CommandSyntaxException e) {
                ChatUtils.errorChatMessage(e.getMessage());
            }
            MinecraftClient.getInstance().inGameHud.getChatHud().addToMessageHistory(message);
            ci.cancel();
        }
    }

    @Redirect(method = "method_34013", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    private void redirectOnResourcePackSend(final MinecraftClient instance, final Screen screen) {
        if (screen instanceof final ConfirmScreen confirmScreen) {
            if (Foxglove.getInstance().getConfigManager().getMainConfig().moreResourcePackOptions.getValue()) {
                instance.setScreen(new CustomResourcePackConfirmScreen(confirmScreen.callback, confirmScreen.getTitle(), confirmScreen.message));
                return;
            }
        }
        instance.setScreen(screen);
    }

}
