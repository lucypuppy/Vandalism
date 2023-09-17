package de.foxglovedevelopment.foxglove.injection.mixins.minecraft;

import de.foxglovedevelopment.foxglove.Foxglove;
import de.foxglovedevelopment.foxglove.feature.impl.module.impl.misc.MessageEncryptorModule;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ChatHud.class)
public abstract class MixinChatHud {

    @ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/ChatMessages;breakRenderedChatMessageLines(Lnet/minecraft/text/StringVisitable;ILnet/minecraft/client/font/TextRenderer;)Ljava/util/List;"))
    public StringVisitable modifyMessage(final StringVisitable content) {
        final MessageEncryptorModule messageEncryptorModule = Foxglove.getInstance().getModuleRegistry().getMessageEncryptorModule();
        if (messageEncryptorModule.isEnabled()) {
            final MutableText text = (MutableText) content;
            final String stringTest = text.getString();
            if (messageEncryptorModule.isEncrypted(stringTest)) {
                return text.append(MessageEncryptorModule.ENCRYPTION_PREFIX.setStyle(MessageEncryptorModule.ENCRYPTION_PREFIX.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Text.literal(messageEncryptorModule.decryptMessage(stringTest))
                ))));
            }
        }
        return content;
    }

}
