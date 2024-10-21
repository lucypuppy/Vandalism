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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.event.game.HandleInputListener;
import de.nekosarekawaii.vandalism.event.player.RotationListener;
import de.nekosarekawaii.vandalism.event.render.Render3DListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.integration.rotation.PrioritizedRotation;
import de.nekosarekawaii.vandalism.integration.rotation.RotationUtil;
import de.nekosarekawaii.vandalism.integration.rotation.enums.RotationPriority;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;

public class ScaffoldModule extends Module implements RotationListener, HandleInputListener, Render3DListener {

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

    private final BooleanValue movementFix = new BooleanValue(
            this.rotationGroup,
            "Movement Fix",
            "Whether the movement fix should be used.",
            true
    );

    private Vec3d posVec = null;
    private BlockPos placeBlock = null;

    public ScaffoldModule() {
        super("Scaffold", "Places blocks underneath you.", Category.MOVEMENT);
        this.markExperimental();
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, RotationEvent.ID, HandleInputEvent.ID, Render3DEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, RotationEvent.ID, HandleInputEvent.ID, Render3DEvent.ID);
        Vandalism.getInstance().getRotationManager().resetRotation(RotationPriority.HIGHEST);
    }


    @Override
    public void onHandleInputEvent(HandleInputEvent event) {
        final BlockPos playerPos = mc.player.getBlockPos();

        this.placeBlock = getPlaceBlock(playerPos, (int) Math.round(mc.player.getBlockInteractionRange()));
        if (this.placeBlock == null) {
            return;
        }

        this.posVec = bestVecFromBlock(this.placeBlock);
        if (this.posVec == null) {
            return;
        }

        mc.doItemUse();
    }

    @Override
    public void onRotation(final RotationEvent event) {
        if (this.posVec != null) {
            final PrioritizedRotation rotation = RotationUtil.rotationToVec(this.posVec, RotationPriority.HIGHEST);

            Vandalism.getInstance().getRotationManager().setRotation(rotation, movementFix.getValue(), (targetRotation, serverRotation, deltaTime, hasClientRotation) ->
                    RotationUtil.rotateMouse(targetRotation, serverRotation, this.rotateSpeed.getValue(), deltaTime, hasClientRotation));
        }
    }

    @Override
    public void onRender3D(float tickDelta, MatrixStack matrixStack) {
        final VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEffectVertexConsumers();
        final VertexConsumer vertexConsumer = immediate.getBuffer(RenderLayer.getLines());

        final Vec3d vec = MinecraftClient.getInstance().gameRenderer.getCamera().getPos().negate();
        matrixStack.push();
        matrixStack.translate(vec.x, vec.y, vec.z);

        if (this.placeBlock != null) {
            WorldRenderer.drawBox(
                    matrixStack,
                    vertexConsumer,
                    this.placeBlock.getX(),
                    this.placeBlock.getY(),
                    this.placeBlock.getZ(),
                    this.placeBlock.getX() + 1,
                    this.placeBlock.getY() + 1,
                    this.placeBlock.getZ() + 1,
                    1.0f, 0.0f, 0.0f, 0.3f
            );
        }

        if (this.posVec != null) {
            WorldRenderer.drawBox(
                    matrixStack,
                    vertexConsumer,
                    this.posVec.getX() - 0.05,
                    this.posVec.getY() - 0.05,
                    this.posVec.getZ() - 0.05,
                    this.posVec.getX() + 0.05,
                    this.posVec.getY() + 0.05,
                    this.posVec.getZ() + 0.05,
                    0.0f, 1.0f, 0.0f, 1.0f
            );
        }

        immediate.draw();
        matrixStack.pop();
    }

    private BlockPos getPlaceBlock(final BlockPos pos, final int scanRange) {
        double bestDistance = Double.MAX_VALUE;
        BlockPos bestBlockPos = null;

        for (int x = -scanRange; x <= scanRange; x++) {
            for (int y = -scanRange; y < 0; y++) {
                for (int z = -scanRange; z <= scanRange; z++) {
                    final BlockPos blockPos = pos.add(x, y, z);
                    final BlockState blockState = mc.world.getBlockState(blockPos);

                    if (!blockState.isSolidBlock(mc.world, blockPos)) {
                        continue;
                    }

                    final double distance = pos.getSquaredDistance(blockPos);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        bestBlockPos = blockPos;
                    }
                }
            }
        }

        return bestBlockPos;
    }

    private Vec3d bestVecFromBlock(final BlockPos blockPos) {
        final BlockState state = mc.world.getBlockState(blockPos);
        final VoxelShape shape = state.getCollisionShape(mc.world, blockPos, ShapeContext.of(mc.player));

        if (shape.isEmpty()) {
            return null;
        }

        final Vec3d eyePos = mc.player.getPos().add(0, mc.player.getEyeHeight(mc.player.getPose()), 0);

        Vec3d closestPoint = null;
        double distance = Double.MAX_VALUE;
        for (final Direction direction : Direction.values()) {
            if (direction == Direction.DOWN) { // Continue if the direction is down
                continue;
            }

            final VoxelShape faceShape = shape.getFace(direction).offset(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if (faceShape.isEmpty()) {
                continue;
            }

            final Box box = faceShape.getBoundingBox().shrink(0.0, 0.3, 0.0);
            final double x = MathHelper.clamp(eyePos.getX(), box.minX, box.maxX);
            final double y = MathHelper.clamp(eyePos.getY(), box.minY, box.maxY);
            final double z = MathHelper.clamp(eyePos.getZ(), box.minZ, box.maxZ);
            final Vec3d vec = new Vec3d(x, y, z);
            final double dist = eyePos.squaredDistanceTo(vec);

            if (dist < distance) {
                distance = dist;
                closestPoint = vec;
            }
        }

        return closestPoint;
    }

}