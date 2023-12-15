package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.ChatSettings;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.render.RenderUtil;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(value = ChatScreen.class, priority = 9999)
public abstract class MixinChatScreen implements MinecraftWrapper {

    @Shadow
    protected TextFieldWidget chatField;

    @Unique
    private int vandalism$realMaxLength = 0;

    @Unique
    private Style vandalism$redColoredStyle = Style.EMPTY.withColor(TextColor.fromRgb(Color.RED.getRGB()));

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void modifyChatFieldMaxLength(final CallbackInfo ci) {
        this.vandalism$realMaxLength = this.chatField.getMaxLength();

        final var chatSettings = Vandalism.getInstance().getClientSettings().getChatSettings();
        if (chatSettings.customChatLength.getValue()) {
            this.chatField.setMaxLength(chatSettings.maxChatLength.getValue());
        }
    }

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void renderTypedCharCounter(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        if (Vandalism.getInstance().getClientSettings().getChatSettings().displayTypedChars.getValue()) {
            final int currentLength = this.chatField.getText().length();

            final var text = Text.literal("" + currentLength + Formatting.DARK_GRAY + " / ");
            text.append(Text.literal("" + vandalism$realMaxLength).setStyle(vandalism$redColoredStyle));
            text.append(Text.literal(Formatting.DARK_GRAY + " (" + Formatting.DARK_RED + this.chatField.getMaxLength() + Formatting.DARK_GRAY + ")"));

            final int x = this.chatField.getX() + this.chatField.getWidth() - this.mc.textRenderer.getWidth(text) - 2;
            final int y = this.chatField.getY() - this.mc.textRenderer.fontHeight - 2;

            final Color color = RenderUtil.interpolateColor(Color.GREEN, Color.YELLOW, Color.RED, Math.min((float) currentLength / this.vandalism$realMaxLength, 1.0F));
            context.drawText(this.mc.textRenderer, text, x, y, color.getRGB(), true);
        }
    }

}