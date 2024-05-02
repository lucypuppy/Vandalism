/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.feature.module.impl.misc.ethanol.impl;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;

public class CommandEthanolCustomPayload implements CustomPayload {

    public static final CustomPayload.Id<CommandEthanolCustomPayload> ID = new Id<>(
            new Identifier("ethanol", "command")
    );

    public static final PacketCodec<PacketByteBuf, CommandEthanolCustomPayload> CODEC = CustomPayload.codecOf(
            CommandEthanolCustomPayload::write,
            buf -> {
                throw new UnsupportedOperationException("CommandEthanolCustomPayload is a write only packet");
            }
    );

    private final String command;

    public CommandEthanolCustomPayload(final String command) {
        this.command = command;
    }

    private void write(final PacketByteBuf buf) {
        buf.writeBytes(command.getBytes(StandardCharsets.UTF_8));
    }

    public CustomPayload.Id<CommandEthanolCustomPayload> getId() {
        return ID;
    }

}
