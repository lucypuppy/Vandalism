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

package de.nekosarekawaii.vandalism.util.game;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.message.LastSeenMessagesCollector;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

import java.time.Instant;


public class SayUtil {

    // This is here to prevent the wrong usage of the ChatUtil class
    public static void sendChatMessage(final String message) {
        final ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (networkHandler == null) return;
        final Instant instant = Instant.now();
        final long secureRandom = NetworkEncryptionUtils.SecureRandomUtil.nextLong();
        final LastSeenMessagesCollector.LastSeenMessages lastSeenMessages = networkHandler.lastSeenMessagesCollector.collect();
        final MessageSignatureData messageSignatureData = networkHandler.messagePacker.pack(new MessageBody(message, instant, secureRandom, lastSeenMessages.lastSeen()));
        networkHandler.sendPacket(new ChatMessageC2SPacket(message, instant, secureRandom, messageSignatureData, lastSeenMessages.update()));
    }

}
