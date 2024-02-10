/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
    private static final Style vandalism$RED_COLORED_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(Color.RED.getRGB()));

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void moreChatInput(final CallbackInfo ci) {
        this.vandalism$realMaxLength = this.chatField.getMaxLength();
        final ChatSettings chatSettings = Vandalism.getInstance().getClientSettings().getChatSettings();
        if (chatSettings.moreChatInput.getValue()) {
            this.chatField.setMaxLength(Integer.MAX_VALUE);
        }
    }

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void renderTypedCharCounter(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        if (Vandalism.getInstance().getClientSettings().getChatSettings().displayTypedChars.getValue()) {
            final int currentLength = this.chatField.getText().length();
            final MutableText text = Text.literal("" + currentLength + Formatting.DARK_GRAY + " / ");
            text.append(Text.literal("" + vandalism$realMaxLength).setStyle(vandalism$RED_COLORED_STYLE));
            text.append(Text.literal(Formatting.DARK_GRAY + " (" + Formatting.DARK_RED + this.chatField.getMaxLength() + Formatting.DARK_GRAY + ")"));
            final int x = this.chatField.getX() + this.chatField.getWidth() - this.mc.textRenderer.getWidth(text) - 2;
            final int y = this.chatField.getY() - this.mc.textRenderer.fontHeight - 2;
            final Color color = RenderUtil.interpolateColor(Color.GREEN, Color.YELLOW, Color.RED, Math.min((float) currentLength / this.vandalism$realMaxLength, 1.0f));
            context.drawText(this.mc.textRenderer, text, x, y, color.getRGB(), true);
        }
    }

}