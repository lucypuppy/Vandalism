package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.player.ChatListener;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChatScreen.class)
public abstract class MixinChatScreen {

    @Redirect(method = "sendMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;normalize(Ljava/lang/String;)Ljava/lang/String;"))
    private String vandalism$callChatSendEvent(final ChatScreen instance, String chatText) {
        chatText = instance.normalize(chatText);
        if (!chatText.isBlank()) {
            final ChatListener.ChatSendEvent chatSendEvent = new ChatListener.ChatSendEvent(chatText);
            DietrichEvents2.global().postInternal(ChatListener.ChatSendEvent.ID, chatSendEvent);
            chatText = chatSendEvent.message;
        }
        return chatText;
    }

}
