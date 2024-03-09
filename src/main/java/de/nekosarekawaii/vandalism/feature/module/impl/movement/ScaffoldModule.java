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
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.event.normal.player.RotationListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import de.nekosarekawaii.vandalism.integration.rotation.enums.RotationPriority;
import de.nekosarekawaii.vandalism.util.game.WorldUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ScaffoldModule extends AbstractModule implements PlayerUpdateListener, RotationListener {

    private BlockPos pos = null;
    private Direction direction = null;
    private Rotation rotation = null;

    public ScaffoldModule() {
        super("Scaffold", "Places blocks underneath you.", Category.MOVEMENT);
        this.markExperimental();
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, RotationEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, RotationEvent.ID);
        Vandalism.getInstance().getRotationListener().resetRotation();
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        this.pos = getPlaceBlock(3);
        if (pos == null) {
            return;
        }

        this.direction = getDirection(mc.player.getPos(), pos);
        if (this.direction == null || this.rotation == null) {
            return;
        }

        final BlockHitResult raycastBlocks = WorldUtil.raytraceBlocks(this.rotation, 5.0);
        if (raycastBlocks.getSide() == this.direction) {
            mc.doItemUse();
        }
    }

    @Override
    public void onPostPlayerUpdate(PlayerUpdateEvent event) {

    }

    @Override
    public void onRotation(RotationEvent event) {
        if (this.pos != null) {
            this.rotation = rotation(pos);

            if (this.rotation == null) {
                return;
            }

            Vandalism.getInstance().getRotationListener().setRotation(this.rotation, RotationPriority.HIGH, 80f, 0, false);
        }
    }

    private BlockPos getPlaceBlock(int scanRange) {
        double distance = -1;
        BlockPos theChosenOne = null;

        for (int x = -scanRange; x < scanRange; x++) {
            for (int y = -scanRange; y < 0; y++) {
                for (int z = -scanRange; z < scanRange; z++) {
                    BlockPos pos = mc.player.getBlockPos().add(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);

                    if (state.isSolidBlock(mc.world, pos)) {
                        double currentDistance = mc.player.getBlockPos().getSquaredDistance(pos);
                        if (distance == -1 || currentDistance < distance) {
                            distance = currentDistance;
                            theChosenOne = pos;
                        }
                    }
                }
            }
        }

        return theChosenOne;
    }

    private Rotation rotation(final BlockPos blockPos) {
        Rotation rotation = null;

        for (int yaw = -45; yaw < 45; yaw++) {
            for (int pitch = 60; pitch < 90; pitch++) {
                Rotation currentRotation = new Rotation(mc.player.getYaw() - (180 + yaw), pitch);
                final BlockHitResult raycastBlocks = WorldUtil.raytraceBlocks(currentRotation, 5.0);

                if (raycastBlocks.getSide() != this.direction || !raycastBlocks.getBlockPos().equals(blockPos)) {
                    continue;
                }

                rotation = currentRotation;
            }
        }

        return rotation;

    }

    public static Direction getDirection(Vec3d playerPos, BlockPos blockpos) {
        final double dx = (blockpos.getX() + 0.5) - playerPos.x;
        final double dz = (blockpos.getZ() + 0.5) - playerPos.z;
        final double maxAxis = Math.max(Math.abs(dx), Math.abs(dz));

        if (maxAxis == Math.abs(dx)) {
            return dx > 0 ? Direction.WEST : Direction.EAST;
        }

        return dz > 0 ? Direction.NORTH : Direction.SOUTH;
    }

}