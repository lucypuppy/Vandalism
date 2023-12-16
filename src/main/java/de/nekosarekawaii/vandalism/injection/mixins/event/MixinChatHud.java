package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.vandalism.base.event.player.ChatModifyReceiveListener;
import de.nekosarekawaii.vandalism.base.event.player.ChatReceiveListener;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class MixinChatHud {

    @ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/ChatMessages;breakRenderedChatMessageLines(Lnet/minecraft/text/StringVisitable;ILnet/minecraft/client/font/TextRenderer;)Ljava/util/List;"))
    public StringVisitable callChatModifyReceiveListener(final StringVisitable content) {
        final ChatModifyReceiveListener.ChatModifyReceiveEvent event = new ChatModifyReceiveListener.ChatModifyReceiveEvent((MutableText) content);
        DietrichEvents2.global().postInternal(ChatModifyReceiveListener.ChatModifyReceiveEvent.ID, event);
        return event.mutableText;
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/ChatMessages;breakRenderedChatMessageLines(Lnet/minecraft/text/StringVisitable;ILnet/minecraft/client/font/TextRenderer;)Ljava/util/List;"))
    public void callChatReceiveListener(final Text message, final MessageSignatureData signature, final int ticks, final MessageIndicator indicator, final boolean refresh, final CallbackInfo ci) {
        final ChatReceiveListener.ChatReceiveEvent event = new ChatReceiveListener.ChatReceiveEvent(message, signature, indicator);
        DietrichEvents2.global().postInternal(ChatReceiveListener.ChatReceiveEvent.ID, event);
    }

}
