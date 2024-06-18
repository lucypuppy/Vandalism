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

package de.nekosarekawaii.vandalism.addonthirdparty.ethanol.module.impl;

import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import de.nekosarekawaii.vandalism.util.game.PacketHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MessageEthanolCustomPayload implements CustomPayload {

    public static final Id<MessageEthanolCustomPayload> ID = new Id<>(
            Identifier.of("ethanol", "message")
    );

    public static final PacketCodec<PacketByteBuf, MessageEthanolCustomPayload> CODEC = CustomPayload.codecOf((value, buf) -> {
        throw new UnsupportedOperationException("MessageEthanolCustomPayload is a read-only packet");
    }, MessageEthanolCustomPayload::new);

    public MessageEthanolCustomPayload(final PacketByteBuf buf) {
        ChatUtil.chatMessage(Text.literal(new String(PacketHelper.readBuffer(buf))));
    }

    public Id<MessageEthanolCustomPayload> getId() {
        return ID;
    }

}
