package me.nekosarekawaii.foxglove.mixin.net.minecraft.client.network;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.util.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(final String message, final CallbackInfo ci) {
        final String prefix = Foxglove.getInstance().getConfigManager().getMainConfig().getCommandPrefix();
        if (message.startsWith(prefix)) {
            try {
                Foxglove.getInstance().getCommandRegistry().commandDispatch(message.substring(prefix.length()));
            } catch (final CommandSyntaxException e) {
                ChatUtils.errorChatMessage(e.getMessage());
            }
            this.client.inGameHud.getChatHud().addToMessageHistory(message);
            ci.cancel();
        }
    }

}
