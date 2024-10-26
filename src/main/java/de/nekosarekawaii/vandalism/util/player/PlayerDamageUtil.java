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

package de.nekosarekawaii.vandalism.util.player;

import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.PacketHelper;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class PlayerDamageUtil implements MinecraftWrapper {

    public static void damagePlayer(final int hearts) {
        if (mc.player == null) {
            return;
        }

        final double x = mc.player.getX();
        final double y = mc.player.getY();
        final double z = mc.player.getZ();

        for (int i = 0; i < calculatePacketsForDamage(hearts); i++) {
            PacketHelper.sendImmediately(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + hearts, z, false), null, true);
            PacketHelper.sendImmediately(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false), null, true);
        }

        PacketHelper.sendImmediately(new PlayerMoveC2SPacket.OnGroundOnly(true), null, true);
    }

    public static void damagePlayerVulcan() {
        final double[] heights = new double[]{0.0, -0.0784, 0.0, +0.41999998688697815, +0.7531999805212, +1.0,
                +1.4199999868869781, +1.7531999805212, +2.0, +2.419999986886978, +2.7531999805212, +3.00133597911214};
        final boolean[] onGrounds = new boolean[]{true, false, true, false, false, true, false, false, true, false, false, false};

        final double x = mc.player.getX();
        final double y = mc.player.getY();
        final double z = mc.player.getZ();

        for (int i = 0; i < heights.length; i++) {
            final double height = heights[i];
            final boolean onGround = onGrounds[i];
            PacketHelper.sendImmediately(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + height, z, onGround), null, true);
        }

        mc.player.setPos(x, y + 3.1, z);
    }

    public static int calculatePacketsForDamage(final int hearts) {
        double fallDistanceReq = 3.1;

        if (mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            final int amplifier = mc.player.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier();
            fallDistanceReq += (float) (amplifier + 1);
        }

        return (int) Math.ceil(fallDistanceReq / hearts);
    }

}
