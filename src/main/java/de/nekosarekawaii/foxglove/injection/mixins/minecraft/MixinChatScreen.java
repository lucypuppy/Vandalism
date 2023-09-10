package de.nekosarekawaii.foxglove.injection.mixins.minecraft;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.event.ChatListener;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChatScreen.class, priority = 2000)
public abstract class MixinChatScreen {

    @Shadow
    protected TextFieldWidget chatField;

    @Shadow
    public abstract String normalize(final String chatText);

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void injectInit(final CallbackInfo ci) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().customChatLength.getValue()) {
            this.chatField.setMaxLength(Foxglove.getInstance().getConfigManager().getMainConfig().maxChatLength.getValue());
        }
    }

    @Redirect(method = "sendMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;normalize(Ljava/lang/String;)Ljava/lang/String;"))
    private String redirectSendMessage(final ChatScreen instance, String chatText) {
        chatText = instance.normalize(chatText);
        if (!chatText.isEmpty()) {
            final ChatListener.ChatSendEvent chatSendEvent = new ChatListener.ChatSendEvent(chatText);
            DietrichEvents2.global().postInternal(ChatListener.ChatSendEvent.ID, chatSendEvent);
            chatText = chatSendEvent.message;
        }
        return chatText;
    }

}
