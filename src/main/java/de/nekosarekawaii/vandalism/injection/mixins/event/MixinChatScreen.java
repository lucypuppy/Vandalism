package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.player.ChatSendListener;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChatScreen.class)
public abstract class MixinChatScreen {

    @Redirect(method = "sendMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;normalize(Ljava/lang/String;)Ljava/lang/String;"))
    private String callChatSendListener(final ChatScreen instance, String chatText) {
        chatText = instance.normalize(chatText);
        if (!chatText.isBlank()) {
            final var event = new ChatSendListener.ChatSendEvent(chatText);
            Vandalism.getInstance().getEventSystem().postInternal(ChatSendListener.ChatSendEvent.ID, event);
            chatText = event.message;
        }
        return chatText;
    }

}
