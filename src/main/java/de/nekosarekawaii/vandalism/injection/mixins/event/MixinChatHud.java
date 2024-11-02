/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.player.ChatModifyReceiveListener;
import de.nekosarekawaii.vandalism.event.player.ChatReceiveListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class MixinChatHud {

    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addVisibleMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V"))
    public ChatHudLine callChatModifyReceiveListener(final ChatHudLine hudLine) {
        if (this.client.player != null) {
            final ChatModifyReceiveListener.ChatModifyReceiveEvent event = new ChatModifyReceiveListener.ChatModifyReceiveEvent(hudLine.content().copy());
            Vandalism.getInstance().getEventSystem().callExceptionally(ChatModifyReceiveListener.ChatModifyReceiveEvent.ID, event);
            return new ChatHudLine(hudLine.creationTick(), event.mutableText, hudLine.signature(), hudLine.indicator());
        }
        return hudLine;
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"), cancellable = true)
    public void callChatReceiveListener(final Text message, final MessageSignatureData signatureData, final MessageIndicator indicator, final CallbackInfo ci) {
        if (this.client.player == null) {
            return;
        }
        final ChatReceiveListener.ChatReceiveEvent event = new ChatReceiveListener.ChatReceiveEvent(message, signatureData, indicator);
        Vandalism.getInstance().getEventSystem().callExceptionally(ChatReceiveListener.ChatReceiveEvent.ID, event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

}
