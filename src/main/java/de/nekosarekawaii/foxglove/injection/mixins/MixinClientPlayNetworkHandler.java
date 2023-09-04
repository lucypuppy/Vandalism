package de.nekosarekawaii.foxglove.injection.mixins;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.feature.impl.module.impl.misc.MessageEncryptorModule;
import de.nekosarekawaii.foxglove.gui.screen.CustomResourcePackConfirmScreen;
import de.nekosarekawaii.foxglove.util.ChatUtils;
import de.nekosarekawaii.foxglove.util.MinecraftWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler implements MinecraftWrapper {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(final String message, final CallbackInfo ci) {
        final String prefix = Foxglove.getInstance().getConfigManager().getMainConfig().commandPrefix.getValue();
        if (message.startsWith(prefix)) {
            try {
                Foxglove.getInstance().getCommandRegistry().commandDispatch(message.substring(prefix.length()));
            } catch (final CommandSyntaxException e) {
                ChatUtils.errorChatMessage(e.getMessage());
            }
            mc().inGameHud.getChatHud().addToMessageHistory(message);
            ci.cancel();
        }
    }

    @ModifyVariable(method = "sendChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/message/MessageChain$Packer;pack(Lnet/minecraft/network/message/MessageBody;)Lnet/minecraft/network/message/MessageSignatureData;", shift = At.Shift.BEFORE), argsOnly = true)
    public String modifyMessage(final String content) {
        final MessageEncryptorModule messageEncryptModule = Foxglove.getInstance().getModuleRegistry().getMessageEncryptModule();
        if (messageEncryptModule.isEnabled()) return messageEncryptModule.encryptMessage(content);
        return content;
    }

    @Redirect(method = "method_34013", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    private void redirectOnResourcePackSend(final MinecraftClient instance, final Screen screen) {
        if (screen instanceof final ConfirmScreen confirmScreen) {
            if (Foxglove.getInstance().getConfigManager().getMainConfig().resourcePackSpoof.getValue()) {
                setScreen(new CustomResourcePackConfirmScreen(confirmScreen.callback, confirmScreen.getTitle(), confirmScreen.message));
                return;
            }
        }
        setScreen(screen);
    }

}
