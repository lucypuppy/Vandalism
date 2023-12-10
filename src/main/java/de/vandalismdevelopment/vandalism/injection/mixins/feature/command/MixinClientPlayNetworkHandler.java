package de.vandalismdevelopment.vandalism.injection.mixins.feature.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.util.ChatUtil;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler implements MinecraftWrapper {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void vandalism$executeModCommands(final String message, final CallbackInfo ci) {
        final String prefix = Vandalism.getInstance().getClientSettings().getChatSettings().commandPrefix.getValue();
        if (message.startsWith(prefix)) {
            try {
                Vandalism.getInstance().getCommandRegistry().execute(message.substring(prefix.length()));
            } catch (CommandSyntaxException e) {
                ChatUtil.errorChatMessage(e.getMessage());
            }
            this.mc.inGameHud.getChatHud().addToMessageHistory(message);
            ci.cancel();
        }
    }

}
