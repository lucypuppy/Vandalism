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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.scaffold;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.event.game.HandleInputListener;
import de.nekosarekawaii.vandalism.event.player.RotationListener;
import de.nekosarekawaii.vandalism.event.render.Render3DListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.scaffold.sneak.LegitSneakModuleMode;
import de.nekosarekawaii.vandalism.feature.module.template.module.ClickerModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleModeValue;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.integration.rotation.PrioritizedRotation;
import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import de.nekosarekawaii.vandalism.integration.rotation.RotationUtil;
import de.nekosarekawaii.vandalism.integration.rotation.enums.RotationPriority;
import de.nekosarekawaii.vandalism.util.MinecraftConstants;
import de.nekosarekawaii.vandalism.util.WorldUtil;
import de.nekosarekawaii.vandalism.util.player.prediction.PredictionSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScaffoldModule extends ClickerModule implements RotationListener, HandleInputListener, Render3DListener {


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

    private final ValueGroup bypassingGroup = new ValueGroup(
            this,
            "Bypassing",
            "Settings for the bypassing."
    );

    private final ModuleModeValue<ScaffoldModule> sneakMode = new ModuleModeValue<>(
            this,
            this.bypassingGroup,
            "Sneak Mode",
            "How the player sneaks. idk",
            new ModuleMulti<>("None") {
            },
            new LegitSneakModuleMode()
    );

    private Vec3d posVec = null;
    private BlockPos placeBlock = null;
    private Box box;

    private final ArrayList<Vec3d> predictedPositionVecs = new ArrayList<>();
    private float bestPitch = Float.NaN;

    public ScaffoldModule() {
        super("Scaffold", "Places blocks underneath you.", Category.MOVEMENT);
        this.markExperimental();
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, RotationEvent.ID, HandleInputEvent.ID, Render3DEvent.ID);
        super.onActivate();
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, RotationEvent.ID, HandleInputEvent.ID, Render3DEvent.ID);
        Vandalism.getInstance().getRotationManager().resetRotation(RotationPriority.HIGHEST);

        super.onDeactivate();
    }

    @Override
    public void onHandleInputEvent(HandleInputEvent event) {
        final Box playerBox = mc.player.getBoundingBox();
        final Vec3d playerPos = mc.player.getPos();
        final BlockPos blockPlayerPos = mc.player.getBlockPos();
        final Vec3d eyePos = playerPos.add(0, mc.player.getEyeHeight(mc.player.getPose()), 0);
        final boolean onGround = playerPos.y % MinecraftConstants.MAGIC_ON_GROUND_MODULO_FACTOR < 0.0001;

        // Prediction stuffs
        final ArrayList<Vec3d> predictedPositions = PredictionSystem.predictState(3, mc.player, null,
                clientPlayerEntity -> false, clientPlayerEntity -> mc.player.getVelocity().y < -0.08).getRight();

        this.placeBlock = getPlaceBlock(playerPos, blockPlayerPos, (int) Math.round(mc.player.getBlockInteractionRange()));
        if (this.placeBlock == null) {
            this.box = null;
            return;
        }

        final BlockState state = mc.world.getBlockState(this.placeBlock);
        final VoxelShape shape = state.getCollisionShape(mc.world, this.placeBlock);
        if (shape.isEmpty()) {
            this.box = null;
            return;
        }

        this.box = shape.getBoundingBox().offset(this.placeBlock.getX(), this.placeBlock.getY(), this.placeBlock.getZ());
        this.posVec = bestVecFromBlock(eyePos, box.offset(0, -0.1, 0)); // Todo maybe find a better offset.

        this.predictedPositionVecs.clear();
        for (final Vec3d position : predictedPositions) {
            final Vec3d predictedEyes = position.add(0, mc.player.getEyeHeight(mc.player.getPose()), 0);
            this.predictedPositionVecs.add(bestVecFromBlock(predictedEyes, box.offset(0, -0.1, 0))); // Todo maybe find a better offset.
        }

        final Rotation rotation = Vandalism.getInstance().getRotationManager().getClientRotation();
        if (rotation == null)
            return;

        final double threshold = 0.1;
        final Box edgeBox = box.shrink(threshold, 0, threshold);
        final boolean isOnEdge = !(edgeBox.minX < playerBox.maxX && edgeBox.maxX > playerBox.minX && edgeBox.minZ < playerBox.maxZ && edgeBox.maxZ > playerBox.minZ);

        if (isOnEdge) {
            mc.doItemUse();
        }
    }

    @Override
    public boolean shouldClick() {
        return Vandalism.getInstance().getRotationManager().getClientRotation() != null && this.posVec != null && this.placeBlock != null;
    }

    @Override
    public void onClick() {
        mc.doItemUse();
    }

    @Override
    public void onFailClick() {
        // Todo IDk maybe only swing.
    }

    @Override
    public void onRotation(final RotationEvent event) {
        if (this.posVec != null) {
            final PrioritizedRotation rotation = RotationUtil.rotationToVec(this.posVec, RotationPriority.HIGHEST);
            final BlockHitResult hitResult = WorldUtil.raytraceBlocks(rotation, 6);

            final Rotation oldRotation = Vandalism.getInstance().getRotationManager().getServerRotation();

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

        for (final Vec3d predictedPosVec : this.predictedPositionVecs) {
            WorldRenderer.drawBox(
                    matrixStack,
                    vertexConsumer,
                    predictedPosVec.getX() - 0.05,
                    predictedPosVec.getY() - 0.05,
                    predictedPosVec.getZ() - 0.05,
                    predictedPosVec.getX() + 0.05,
                    predictedPosVec.getY() + 0.05,
                    predictedPosVec.getZ() + 0.05,
                    0.0f, 0.0f, 1.0f, 1.0f
            );
        }

        immediate.draw();
        matrixStack.pop();
    }

    private BlockPos getPlaceBlock(final Vec3d playerPos, final BlockPos blockPlayerPos, final int scanRange) {
        final BlockPos blockBelow = blockPlayerPos.down();

        if (!mc.world.getBlockState(blockBelow).isAir())
            return blockBelow;

        final List<BlockPos> possibilities = new ArrayList<>();
        for (int x = -scanRange; x <= scanRange; ++x) {
            for (int y = -scanRange; y <= 0; ++y) {
                for (int z = -scanRange; z <= scanRange; ++z) {
                    final BlockPos blockpos = blockPlayerPos.add(x, y, z);

                    if (!mc.world.getBlockState(blockpos).isAir())
                        possibilities.add(blockpos);
                }
            }
        }

        possibilities.removeIf(blockpos ->
                Math.sqrt(playerPos.squaredDistanceTo(blockpos.getX(), blockpos.getY(), blockpos.getZ())) > 5 ||
                        mc.world.getBlockState(blockpos).isAir() ||
                        blockpos.getY() > playerPos.getY());

        if (possibilities.isEmpty())
            return null;

        possibilities.sort(Comparator.comparingDouble(blockpos -> blockpos.getSquaredDistance(playerPos)));
        return possibilities.getFirst();
    }

    private Vec3d bestVecFromBlock(final Vec3d eyePos, final Box box) {
        final double x = MathHelper.clamp(eyePos.getX(), box.minX, box.maxX);
        final double y = MathHelper.clamp(eyePos.getY(), box.minY, box.maxY);
        final double z = MathHelper.clamp(eyePos.getZ(), box.minZ, box.maxZ);
        return new Vec3d(x, y, z);
    }

}