package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.event.ChatListener;
import de.vandalismdevelopment.vandalism.util.render.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(value = ChatScreen.class, priority = 2000)
public abstract class MixinChatScreen {

    @Shadow
    protected TextFieldWidget chatField;

    @Unique
    private int realMaxLength = 0;

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void injectInit(final CallbackInfo ci) {
        this.realMaxLength = this.chatField.getMaxLength();
        if (Vandalism.getInstance().getConfigManager().getMainConfig().chatCategory.customChatLength.getValue()) {
            this.chatField.setMaxLength(Vandalism.getInstance().getConfigManager().getMainConfig().chatCategory.maxChatLength.getValue());
        }
    }

    @Redirect(method = "sendMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;normalize(Ljava/lang/String;)Ljava/lang/String;"))
    private String redirectSendMessage(final ChatScreen instance, String chatText) {
        chatText = instance.normalize(chatText);
        if (!chatText.isBlank()) {
            final ChatListener.ChatSendEvent chatSendEvent = new ChatListener.ChatSendEvent(chatText);
            DietrichEvents2.global().postInternal(ChatListener.ChatSendEvent.ID, chatSendEvent);
            chatText = chatSendEvent.message;
        }
        return chatText;
    }

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void injectRender(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        if (Vandalism.getInstance().getConfigManager().getMainConfig().chatCategory.displayTypedChars.getValue()) {
            final int current = this.chatField.getText().length();
            final MutableText text = Text.literal(Formatting.RESET.toString() + current + Formatting.DARK_GRAY + " / ")
                    .append(
                            Text.literal(String.valueOf(this.realMaxLength)
                            ).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Color.RED.getRGB()))))
                    .append(
                            Text.literal(
                                    Formatting.DARK_GRAY +
                                            " (" + Formatting.DARK_RED +
                                            this.chatField.getMaxLength() +
                                            Formatting.DARK_GRAY + ")"
                            )
                    );
            context.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    text,
                    this.chatField.getX() + this.chatField.getWidth() - MinecraftClient.getInstance().textRenderer.getWidth(text) - 2,
                    this.chatField.getY() - MinecraftClient.getInstance().textRenderer.fontHeight - 2,
                    ColorUtils.interpolate(Color.GREEN, Color.YELLOW, Color.RED, Math.min((float) current / this.realMaxLength, 1.0f)),
                    true
            );
        }
    }

}
