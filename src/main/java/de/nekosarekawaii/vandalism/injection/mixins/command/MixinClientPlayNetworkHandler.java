/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

package de.nekosarekawaii.vandalism.injection.mixins.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.injection.access.IClientPlayNetworkHandler;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler implements IClientPlayNetworkHandler, MinecraftWrapper {

    @Shadow public abstract void sendChatMessage(String content);

    @Unique
    private boolean vandalism$selfInflicting = false;

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void executeClientCommands(final String message, final CallbackInfo ci) {
        if (vandalism$selfInflicting) {
            vandalism$selfInflicting = false;
            return;
        }
        final String prefix = Vandalism.getInstance().getClientSettings().getChatSettings().commandPrefix.getValue();
        if (message.startsWith(prefix) && MinecraftClient.getInstance().currentScreen instanceof ChatScreen) {
            try {
                Vandalism.getInstance().getCommandManager().getCommandDispatcher().execute(message.substring(prefix.length()), AbstractCommand.COMMAND_SOURCE);
            } catch (CommandSyntaxException e) {
                ChatUtil.errorChatMessage(e.getMessage());
            }
            this.mc.inGameHud.getChatHud().addToMessageHistory(message);
            ci.cancel();
        }
    }

    @Override
    public void vandalism$sendChatMessage(String message) {
        vandalism$selfInflicting = true;
        sendChatMessage(message);
    }

}
