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
import de.nekosarekawaii.vandalism.base.account.AbstractAccount;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.ChatSettings;
import de.nekosarekawaii.vandalism.util.render.GLStateTracker;
import de.nekosarekawaii.vandalism.util.render.PlayerSkinRenderer;
import de.nekosarekawaii.vandalism.util.render.RenderUtil;
import de.nekosarekawaii.vandalism.util.wrapper.MinecraftWrapper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.*;

@Mixin(value = ChatScreen.class, priority = 9999)
public abstract class MixinChatScreen extends Screen implements MinecraftWrapper {

    @Shadow
    protected TextFieldWidget chatField;

    @Unique
    private int vandalism$realMaxLength = 0;

    @Unique
    private static final Style vandalism$RED_COLORED_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(Color.RED.getRGB()));

    protected MixinChatScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void customChatInputField(final CallbackInfo ci) {
        this.chatField.setWidth(this.width - 13); //  Fix for the chat input field width
        this.vandalism$realMaxLength = this.chatField.getMaxLength();
        final ChatSettings chatSettings = Vandalism.getInstance().getClientSettings().getChatSettings();
        if (chatSettings.moreChatInput.getValue()) {
            this.chatField.setMaxLength(Integer.MAX_VALUE);
        }
        if (chatSettings.displayAccountHead.getValue()) {
            final int xOffset = 18;
            this.chatField.setX(xOffset);
            this.chatField.setWidth(this.chatField.getWidth() - xOffset);
        }
    }

    @ModifyConstant(method = "init", constant = @Constant(intValue = 10))
    private int moreChatInputSuggestions(final int constant) {
        if (Vandalism.getInstance().getClientSettings().getChatSettings().moreChatInputSuggestions.getValue()) {
            return (this.height - 12 - 3) / 12;
        } else {
            return constant;
        }
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"))
    private void modifyChatFieldBackground(final Args args) {
        if (Vandalism.getInstance().getClientSettings().getChatSettings().displayAccountHead.getValue()) {
            args.set(0, this.chatField.getX() - 2);

        }
    }

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void customChatAreaRendering(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        final ChatSettings chatSettings = Vandalism.getInstance().getClientSettings().getChatSettings();
        if (chatSettings.displayTypedChars.getValue()) {
            final int currentLength = this.chatField.getText().length();
            final MutableText text = Text.literal(String.valueOf(currentLength) + Formatting.DARK_GRAY + " / ");
            text.append(Text.literal(String.valueOf(this.vandalism$realMaxLength)).setStyle(vandalism$RED_COLORED_STYLE));
            final int x = this.chatField.getX() + this.chatField.getWidth() - this.mc.textRenderer.getWidth(text) - 2;
            final int y = this.chatField.getY() - this.mc.textRenderer.fontHeight - 2;
            final Color color = RenderUtil.interpolateColor(
                    Color.GREEN,
                    Color.YELLOW,
                    Color.RED,
                    Math.min((float) currentLength / this.vandalism$realMaxLength, 1.0f)
            );
            context.drawText(this.mc.textRenderer, text, x, y, color.getRGB(), true);
        }
        if (chatSettings.displayAccountHead.getValue()) {
            final AbstractAccount currentAccount = Vandalism.getInstance().getAccountManager().getCurrentAccount();
            if (currentAccount != null) {
                final PlayerSkinRenderer accountPlayerSkin = currentAccount.getPlayerSkin();
                if (accountPlayerSkin != null) {
                    final Identifier playerSkin = accountPlayerSkin.getSkin();
                    if (playerSkin != null) {
                        GLStateTracker.BLEND.save(true);
                        PlayerSkinDrawer.draw(context, playerSkin, 0, this.chatField.getY() - 4, 15, true, false);
                        GLStateTracker.BLEND.revert();
                    }
                }
            }
        }
    }

}