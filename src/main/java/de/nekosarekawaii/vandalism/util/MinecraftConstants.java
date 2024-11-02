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

package de.nekosarekawaii.vandalism.util;

public class MinecraftConstants {

    public static final int MAX_USERNAME_LENGTH = 16;
    public static final int MIN_USERNAME_LENGTH = 3;

    // those constant gets used in shitty anti cheats with no proper collision handling, posY % const == 0 = ground True
    public static final double MAGIC_ON_GROUND_MODULO_FACTOR = 0.015625;

    public static final int FIRST_SLOT_IN_HOTBAR = 36;
    public static final int LAST_SLOT_IN_HOTBAR = 44;

    public static final String TEXTURE_ENDPOINT = "textures.minecraft.net";
    public static final String UUID_ENDPOINT = "https://api.mojang.com/users/profiles/minecraft/";

}
