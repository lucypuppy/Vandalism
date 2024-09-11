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

package de.nekosarekawaii.vandalism.integration.friends;

import de.nekosarekawaii.vandalism.util.UUIDUtil;
import de.nekosarekawaii.vandalism.util.render.util.PlayerSkinRenderer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.Uuids;

import java.util.UUID;

@Getter
public class Friend {

    private final String name;
    private final UUID uuid;

    @Setter
    private String alias;

    private final PlayerSkinRenderer playerSkin;

    public Friend(final String name, final String alias) {
        this.name = name;
        this.alias = alias;
        UUID uuid;
        try {
            uuid = UUID.fromString(UUIDUtil.getUUIDFromName(name));
        } catch (final Exception ignored) {
            uuid = Uuids.getOfflinePlayerUuid(name);
        }
        this.uuid = uuid;
        this.playerSkin = new PlayerSkinRenderer(this.uuid);
    }

}
