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

import de.nekosarekawaii.vandalism.addonthirdparty.AddonThirdParty;
import de.nekosarekawaii.vandalism.util.game.PacketHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

public class InitEthanolCustomPayload implements CustomPayload {

    public static final PacketCodec<PacketByteBuf, InitEthanolCustomPayload> CODEC = CustomPayload.codecOf(InitEthanolCustomPayload::write, InitEthanolCustomPayload::new);
    public static final Id<InitEthanolCustomPayload> ID = new Id<>(new Identifier("ethanol", "init"));

    public InitEthanolCustomPayload() {
    }

    public InitEthanolCustomPayload(final PacketByteBuf buf) {
        AddonThirdParty.getInstance().getEthanolModule().detected = true;
        PacketHelper.sendImmediately(new CustomPayloadC2SPacket(new InitEthanolCustomPayload()), null, true);
    }

    private void write(final PacketByteBuf buf) {}

    public Id<InitEthanolCustomPayload> getId() {
        return ID;
    }

}

