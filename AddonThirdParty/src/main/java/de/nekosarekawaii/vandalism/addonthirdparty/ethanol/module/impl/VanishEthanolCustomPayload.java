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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class VanishEthanolCustomPayload implements CustomPayload {

    public static final Id<VanishEthanolCustomPayload> ID = new Id<>(
            new Identifier("ethanol", "vanish")
    );

    public static final PacketCodec<PacketByteBuf, VanishEthanolCustomPayload> CODEC = CustomPayload.codecOf((value, buf) -> {
        throw new UnsupportedOperationException("VanishEthanolCustomPayload is a read-only packet");
    }, VanishEthanolCustomPayload::new);

    public VanishEthanolCustomPayload(final PacketByteBuf buf) {
        AddonThirdParty.getInstance().getEthanolModule().vanished = buf.readByte() == 1;
    }

    public Id<VanishEthanolCustomPayload> getId() {
        return ID;
    }

}
