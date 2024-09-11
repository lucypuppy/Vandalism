/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.injection.mixins.clientsetting;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.ChatSettings;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(ChatHud.class)
public abstract class MixinChatHud {

    @Shadow
    @Final
    private List<ChatHudLine.Visible> visibleMessages;

    @Unique
    private final List<OrderedText> vandalism$visibleMessages = new ArrayList<>();

    @Redirect(method = "logChatMessage", at = @At(value = "INVOKE", target = "Ljava/lang/String;replaceAll(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", ordinal = 1))
    private String removeSameLineID(String string, final String regex, final String replacement) {
        final ChatSettings chatSettings = Vandalism.getInstance().getClientSettings().getChatSettings();
        if (chatSettings.sameLineMessages.getValue()) {
            if (string.contains(ChatUtil.SAME_LINE_ID.getString())) {
                string = string.replace(ChatUtil.SAME_LINE_ID.getString(), "");
            }
        }
        return string;
    }

    @Redirect(method = "addVisibleMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/ChatMessages;breakRenderedChatMessageLines(Lnet/minecraft/text/StringVisitable;ILnet/minecraft/client/font/TextRenderer;)Ljava/util/List;"))
    private List<OrderedText> sameLineMessages(final StringVisitable message, final int width, final TextRenderer textRenderer) {
        final ChatSettings chatSettings = Vandalism.getInstance().getClientSettings().getChatSettings();
        if (chatSettings.sameLineMessages.getValue()) {
            final Text text = (Text) message;
            if (text.getSiblings().contains(ChatUtil.SAME_LINE_ID)) {
                text.getSiblings().remove(ChatUtil.SAME_LINE_ID);
                final int maxSameLineMessages = chatSettings.maxSameLineMessages.getValue() - 1;
                if (this.visibleMessages.size() > maxSameLineMessages) {
                    for (int i = maxSameLineMessages; i < this.visibleMessages.size(); i++) {
                        final ChatHudLine.Visible visibleMessage = this.visibleMessages.get(i);
                        if (this.vandalism$visibleMessages.contains(visibleMessage.content())) {
                            this.visibleMessages.remove(visibleMessage);
                            break;
                        }
                    }
                }
                final List<OrderedText> lines = ChatMessages.breakRenderedChatMessageLines(text, width, textRenderer);
                this.vandalism$visibleMessages.addAll(lines);
                return lines;
            }
        }
        return ChatMessages.breakRenderedChatMessageLines(message, width, textRenderer);
    }

    @ModifyConstant(method = "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", constant = @Constant(intValue = 100))
    public int moreChatHistory1(final int original) {
        final ChatSettings chatSettings = Vandalism.getInstance().getClientSettings().getChatSettings();
        if (chatSettings.moreChatHistory.getValue()) {
            return chatSettings.moreChatHistoryMaxLength.getValue();
        }
        return original;
    }

    @ModifyConstant(method = "addVisibleMessage", constant = @Constant(intValue = 100))
    public int moreChatHistory2(final int original) {
        final ChatSettings chatSettings = Vandalism.getInstance().getClientSettings().getChatSettings();
        if (chatSettings.moreChatHistory.getValue()) {
            return chatSettings.moreChatHistoryMaxLength.getValue();
        }
        return original;
    }

}
