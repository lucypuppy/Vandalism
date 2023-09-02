package de.nekosarekawaii.foxglove.injection.mixins;

import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.feature.impl.module.impl.misc.MessageEncryptModule;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ChatHud.class)
public abstract class MixinChatHud {

    @ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/ChatMessages;breakRenderedChatMessageLines(Lnet/minecraft/text/StringVisitable;ILnet/minecraft/client/font/TextRenderer;)Ljava/util/List;"))
    public StringVisitable modifyMessage(final StringVisitable content) {
        final MessageEncryptModule messageEncryptModule = Foxglove.getInstance().getModuleRegistry().getMessageEncryptModule();
        final String text = content.getString();

        if (messageEncryptModule.isEnabled() && messageEncryptModule.isEncrypted(text))
            return Text.of(messageEncryptModule.decodeMessage(text));

        return content;
    }

}
