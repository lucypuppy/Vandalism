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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.event.normal.player.RotationListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.integration.newrotation.Rotation;
import de.nekosarekawaii.vandalism.integration.newrotation.RotationUtil;
import de.nekosarekawaii.vandalism.util.game.WorldUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;

public class ScaffoldModule extends AbstractModule implements PlayerUpdateListener, RotationListener {

    private BlockPos pos = null;
    private Vec3d posVec = null;
    private Direction direction = null;
    private Rotation rotation = null;
    private AutoSprintModule autoSprintModule;
    private Rotation prevRotation;

    public ScaffoldModule() {
        super("Scaffold", "Places blocks underneath you.", Category.MOVEMENT);
        this.markExperimental();
    }

    private final ValueGroup rotationGroup = new ValueGroup(
            this,
            "Rotation",
            "Settings for the rotations."
    );

    private final FloatValue rotateSpeed = new FloatValue(
            this.rotationGroup,
            "Rotate Speed",
            "The speed of the rotation.",
            60.0f,
            1.0f,
            180.0f
    );

    private final ValueGroup windMouseGroup = new ValueGroup(
            this.rotationGroup,
            "Wind Mouse Settings",
            "Settings for the WindMouse algorithm."
    );

    private final BooleanValue windMouse = new BooleanValue(
            this.windMouseGroup,
            "Wind Mouse",
            "Whether the WindMouse algorithm should be used.",
            true);

    private final FloatValue gravitationalForce = new FloatValue(
            this.windMouseGroup,
            "Gravitational Force",
            "The strength pf the Gravitational Force.",
            9f,
            0.0f,
            20.0f
    ).visibleCondition(this.windMouse::getValue);

    private final FloatValue windForceMagnitude = new FloatValue(
            this.windMouseGroup,
            "Wind Force Magnitude",
            "The strength of the Wind Force Magnitude.",
            3f,
            0.0f,
            20.0f
    ).visibleCondition(this.windMouse::getValue);

    private final FloatValue maxStepSize = new FloatValue(
            this.windMouseGroup,
            "Max Step Size",
            "The Max Step Size.",
            15f,
            0.0f,
            20.0f
    ).visibleCondition(this.windMouse::getValue);

    private final FloatValue distanceThreshold = new FloatValue(
            this.windMouseGroup,
            "Distance threshold",
            "The Distance Threshold.",
            12f,
            0.0f,
            20.0f
    ).visibleCondition(this.windMouse::getValue);

    private final BooleanValue movementFix = new BooleanValue(
            this.rotationGroup,
            "Movement Fix",
            "Whether the movement fix should be used.",
            true
    );

    public BooleanValue allowSprint = new BooleanValue(
            this,
            "Allow Sprint",
            "Allows you to sprint while scaffolding.",
            false
    );

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, RotationEvent.ID);
        this.autoSprintModule = Vandalism.getInstance().getModuleManager().getByClass(AutoSprintModule.class);
        if (mc.player != null) {
            this.prevRotation = new Rotation(mc.player.prevYaw, mc.player.prevPitch);
        }
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, RotationEvent.ID);
        Vandalism.getInstance().getRotationManager().resetRotation();
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        autoSprintModule.stopSprinting(!allowSprint.getValue());
        final Pair<Vec3d, BlockPos> placeBlock = getPlaceBlock(6);

        if (placeBlock == null) {
            return;
        }

        this.posVec = placeBlock.getLeft();
        this.pos = placeBlock.getRight();

        if (this.pos == null) {
            return;
        }

        this.direction = getDirection(mc.player.getPos(), pos);

        if (this.direction == null || Vandalism.getInstance().getRotationManager().getRotation() == null) {
            return;
        }

        final BlockHitResult raycastBlocks = WorldUtil.raytraceBlocks(Vandalism.getInstance().getRotationManager().getRotation(), 5.0);
        if (raycastBlocks.getBlockPos().equals(this.pos)) {
            if (raycastBlocks.getSide() == this.direction || mc.options.jumpKey.isPressed()) {
                mc.doItemUse();
            }
        }
    }

    @Override
    public void onPostPlayerUpdate(PlayerUpdateEvent event) {

    }

    @Override
    public void onRotation(RotationEvent event) {
        if (this.pos != null) {
            this.rotation = rotation(this.pos);

            if (this.rotation == null) {
                return;
            }

            if(windMouse.getValue()) {
                this.rotation = RotationUtil.windMouseSmooth(
                        rotation, prevRotation,
                        gravitationalForce.getValue(), windForceMagnitude.getValue(), maxStepSize.getValue(), distanceThreshold.getValue()
                );
            }
            prevRotation = rotation;
            Vandalism.getInstance().getRotationManager().setRotation(this.rotation, rotateSpeed.getValue(), 0.0f, movementFix.getValue());
        }
    }

    private Pair<Vec3d, BlockPos> getPlaceBlock(int scanRange) {
        double distance = -1;
        Pair<Vec3d, BlockPos> theChosenOne = null;

        for (int x = -scanRange; x < scanRange; x++) {
            for (int y = -scanRange; y < 0; y++) {
                for (int z = -scanRange; z < scanRange; z++) {
                    final BlockPos pos = mc.player.getBlockPos().add(x, y, z);
                    final BlockState state = mc.world.getBlockState(pos);

                    if (!state.isSolidBlock(mc.world, pos)) {
                        continue;
                    }

                    final VoxelShape shape = state.getCollisionShape(mc.world, pos);
                    final Box box = shape.getBoundingBox().offset(pos);

                    // Best hit vector for scaffold mu haha
                    final double nearestX = MathHelper.clamp(mc.player.getX(), box.minX, box.maxX);
                    final double nearestY = MathHelper.clamp(mc.player.getY(), box.minY, box.maxY);
                    final double nearestZ = MathHelper.clamp(mc.player.getZ(), box.minZ, box.maxZ);

                    final Vec3d nearestPoint = new Vec3d(nearestX, nearestY, nearestZ);
                    final double currentDistance = mc.player.getPos().distanceTo(nearestPoint);

                    if (distance == -1 || currentDistance < distance) {
                        distance = currentDistance;
                        theChosenOne = new Pair<>(nearestPoint, pos);
                    }
                }
            }
        }

        return theChosenOne;
    }

    private Rotation rotation(final BlockPos blockPos) {
        Rotation rotation = null;

        for (int yaw = -45; yaw < 45; yaw++) {
            for (int pitch = 0; pitch < 90; pitch++) {
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