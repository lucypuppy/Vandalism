package de.vandalismdevelopment.vandalism.injection.mixins.feature.config;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.util.RenderUtil;
import de.vandalismdevelopment.vandalism.util.interfaces.MinecraftWrapper;
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
    private int realMaxLength = 0;

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void vandalism$modifyChatFieldMaxLength(final CallbackInfo ci) {
        this.realMaxLength = this.chatField.getMaxLength();
        if (Vandalism.getInstance().getConfigManager().getMainConfig().chatCategory.customChatLength.getValue()) {
            this.chatField.setMaxLength(Vandalism.getInstance().getConfigManager().getMainConfig().chatCategory.maxChatLength.getValue());
        }
    }

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void vandalism$renderTypedCharCounter(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        if (Vandalism.getInstance().getConfigManager().getMainConfig().chatCategory.displayTypedChars.getValue()) {
            final int current = this.chatField.getText().length();
            final MutableText text = Text.literal(Formatting.RESET.toString() + current + Formatting.DARK_GRAY + " / ");
            text.append(Text.literal(String.valueOf(this.realMaxLength)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Color.RED.getRGB()))));
            text.append(Text.literal(Formatting.DARK_GRAY + " (" + Formatting.DARK_RED + this.chatField.getMaxLength() + Formatting.DARK_GRAY + ")"));
            final int x = this.chatField.getX() + this.chatField.getWidth() - this.textRenderer().getWidth(text) - 2;
            final int y = this.chatField.getY() - this.textRenderer().fontHeight - 2;
            final int color = RenderUtil.interpolateColor(Color.GREEN, Color.YELLOW, Color.RED, Math.min((float) current / this.realMaxLength, 1.0f));
            context.drawText(this.textRenderer(), text, x, y, color, true);
        }
    }

}