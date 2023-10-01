package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.util.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(final String message, final CallbackInfo ci) {
        final String prefix = Vandalism.getInstance().getConfigManager().getMainConfig().commandPrefix.getValue();
        if (message.startsWith(prefix)) {
            try {
                Vandalism.getInstance().getCommandRegistry().commandDispatch(message.substring(prefix.length()));
            } catch (final CommandSyntaxException e) {
                ChatUtils.errorChatMessage(e.getMessage());
            }
            MinecraftClient.getInstance().inGameHud.getChatHud().addToMessageHistory(message);
            ci.cancel();
        }
    }

}
