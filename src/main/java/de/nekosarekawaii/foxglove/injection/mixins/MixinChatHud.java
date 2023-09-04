package de.nekosarekawaii.foxglove.injection.mixins;

import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.util.MessageEncryptUtil;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ChatHud.class)
public abstract class MixinChatHud {

    @Unique
    private final static MutableText ENCRYPTION_PREFIX = Text.empty()
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append(" [")
            .append(Text.literal("E").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xff2626))))
            .append("]");

    @ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/ChatMessages;breakRenderedChatMessageLines(Lnet/minecraft/text/StringVisitable;ILnet/minecraft/client/font/TextRenderer;)Ljava/util/List;"))
    public StringVisitable modifyMessage(final StringVisitable content) {
        if (Foxglove.getInstance().getModuleRegistry().getMessageEncryptModule().isEnabled()) {
            final MutableText text = (MutableText) content;
            final String stringTest = text.getString();
            if (MessageEncryptUtil.isEncrypted(stringTest)) {
                return text.append(ENCRYPTION_PREFIX.setStyle(ENCRYPTION_PREFIX.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Text.literal(MessageEncryptUtil.decodeMessage(stringTest))
                ))));
            }
        }
        return content;
    }

}
