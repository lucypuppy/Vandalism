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

package de.nekosarekawaii.vandalism.util.player;

import de.nekosarekawaii.vandalism.util.MinecraftConstants;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;

public class PlayerUtil implements MinecraftWrapper {

    public static boolean isOnGround(final double offset) {
        if (mc.world == null || mc.player == null) {
            return false;
        }

        return mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(0.0D, -offset, 0.0D)).iterator().hasNext();
    }

    public static boolean isMathematicallyOnGround(final double offset) {
        if (mc.world == null || mc.player == null) {
            return false;
        }

        return mc.player.getY() % MinecraftConstants.MAGIC_ON_GROUND_MODULO_FACTOR < offset;
    }

    /**
     * Get the speed related to the yaw.
     * @param posY The position y to use.
     * @return The speed related to the yaw.
     */
    public static double roundToGround(final double posY) {
        return Math.round(posY / MinecraftConstants.MAGIC_ON_GROUND_MODULO_FACTOR) * MinecraftConstants.MAGIC_ON_GROUND_MODULO_FACTOR;
    }


}
