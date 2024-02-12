/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class TeleportModule extends AbstractModule implements PlayerUpdateListener {

    private final FloatValue maxDistance = new FloatValue(
            this,
            "Max Distance",
            "Max teleport distance.",
            10.0f,
            0.0f,
            100.0f
    );

    public TeleportModule() {
        super("Teleport", "Teleport to the block you click on.", Category.MOVEMENT);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        if (mc.player.isUsingItem() || !mc.options.useKey.isPressed()) return;

        HitResult result = mc.player.raycast(maxDistance.getValue(), mc.getTickDelta(), false);
        if (result.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) result).getBlockPos();

            Vec3d finalPos = new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);


            double dis = mc.player.getPos().distanceTo(result.getPos());

            for (double d = 0.0D; d < dis; d += 2.0D) {
                teleportNormal(
                        mc.player.getX() + (finalPos.x - (double) mc.player.getHorizontalFacing().getOffsetX() - mc.player.getX()) * d / dis,
                        mc.player.getY() + (finalPos.y - mc.player.getY()) * d / dis,
                        mc.player.getZ() + (finalPos.z - (double) mc.player.getHorizontalFacing().getOffsetZ() - mc.player.getZ()) * d / dis
                );
            }
            teleportNormal(finalPos.x, finalPos.y, finalPos.z);
            mc.options.useKey.setPressed(false);
        }
    }

    public static void teleportNormal(double x, double y, double z) {
        mc.getNetworkHandler().getConnection().channel.writeAndFlush(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true));
        mc.player.setPosition(x, y, z);
    }
}
