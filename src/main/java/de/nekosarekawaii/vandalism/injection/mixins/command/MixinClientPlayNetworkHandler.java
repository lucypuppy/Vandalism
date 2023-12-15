package de.nekosarekawaii.vandalism.injection.mixins.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.minecraft.ChatUtil;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler implements MinecraftWrapper {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void executeModCommands(final String message, final CallbackInfo ci) {
        final String prefix = Vandalism.getInstance().getClientSettings().getChatSettings().commandPrefix.getValue();
        if (message.startsWith(prefix)) {
            try {
                Vandalism.getInstance().getCommandManager().getCommandDispatcher().execute(message.substring(prefix.length()), AbstractCommand.COMMAND_SOURCE);
            } catch (CommandSyntaxException e) {
                ChatUtil.errorChatMessage(e.getMessage());
            }
            this.mc.inGameHud.getChatHud().addToMessageHistory(message);
            ci.cancel();
        }
    }

}
