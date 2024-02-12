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
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

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
            Direction side = ((BlockHitResult) result).getSide();

            BlockState state = mc.world.getBlockState(pos);

            VoxelShape shape = state.getCollisionShape(mc.world, pos);
            if (shape.isEmpty()) shape = state.getOutlineShape(mc.world, pos);

            double height = shape.isEmpty() ? 1 : shape.getMax(Direction.Axis.Y);

//            mc.player.setPosition(pos.getX() + 0.5 + side.getOffsetX(), pos.getY() + height, pos.getZ() + 0.5 + side.getOffsetZ());

            Vec3d finalPos = new Vec3d(pos.getX() + 0.5 + side.getOffsetX(), pos.getY() + height, pos.getZ() + 0.5 + side.getOffsetZ());


            double distance = mc.player.getPos().distanceTo(result.getPos());

            int steps = (int) Math.ceil(distance / 9);


            Vec3d lastPos = mc.player.getPos();
            for (int i = 1; i <= steps; i++) {
                ChatUtil.chatMessage(i + " " + steps + " " + distance);
                Vec3d newPos = new Vec3d(
                        lastPos.getX() + i * (finalPos.x - mc.player.getX()),
                        lastPos.getY() + i * (finalPos.y - mc.player.getY()),
                        lastPos.getZ() + i * (finalPos.z - mc.player.getZ())
                );

                mc.player.updatePosition(newPos.x, newPos.y, newPos.z);
                mc.getNetworkHandler().getConnection().channel.writeAndFlush(new PlayerMoveC2SPacket.PositionAndOnGround(newPos.x, newPos.y, newPos.z, mc.player.isOnGround()));
                lastPos = newPos;
            }
            mc.options.useKey.setPressed(false);
        }
    }
}
