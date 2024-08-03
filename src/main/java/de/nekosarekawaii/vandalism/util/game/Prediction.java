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

package de.nekosarekawaii.vandalism.util.game;

import de.nekosarekawaii.vandalism.injection.access.ILivingEntity;
import de.nekosarekawaii.vandalism.util.math.MCMathUtil;
import net.minecraft.client.input.Input;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class Prediction {

    public static LivingEntity predictEntityMovement(LivingEntity entity, int ticks, boolean predictInput, boolean predictJumping) {
        final LivingEntity livingEntity = new LivingEntity((EntityType<? extends LivingEntity>) entity.getType(), entity.getWorld()) {
            @Override
            public Iterable<ItemStack> getArmorItems() {
                return entity.getArmorItems();
            }

            @Override
            public ItemStack getEquippedStack(EquipmentSlot slot) {
                return entity.getEquippedStack(slot);
            }

            @Override
            public void equipStack(EquipmentSlot slot, ItemStack stack) {
                // Do nothing
            }

            @Override
            public Arm getMainArm() {
                return entity.getMainArm();
            }
        };

        livingEntity.copyFrom(entity);
        livingEntity.copyPositionAndRotation(entity);
        livingEntity.setVelocity(entity.getVelocity());

        for (int i = 0; i < ticks; i++) {
            if(predictInput) {
                BlockPos blockPos = entity.getVelocityAffectingPos();
                float p = entity.getWorld().getBlockState(blockPos).getBlock().getSlipperiness();
                Vec3d input = new Vec3d(entity.sidewaysSpeed, entity.upwardSpeed, entity.forwardSpeed);
                livingEntity.applyMovementInput(input, p);
            }
            if (predictJumping && livingEntity.isOnGround()) {
                livingEntity.jump();
            }
            livingEntity.tick();
        }

        return livingEntity;
    }

    public static Vec3d predictEntityPosition(LivingEntity entity, int ticks, boolean predictInput, boolean predictJumping) {
        LivingEntity predictedEntity = predictEntityMovement(entity, ticks, predictInput, predictJumping);
        return predictedEntity.getPos();
    }

    // Jump/sneak states are randomized as soon as getClosestInput is called
    private static final List<Input> BRUTEFORCE_INPUTS = MCMathUtil.possibleInputs();

    public static Input getClosestInput(final LivingEntity baseEntity) {
        final Vec3d serverPos = ((ILivingEntity) baseEntity).vandalism$prevServerPos();
        if (serverPos == null) {
            return new Input();
        }

        final Vec3d velocity = new Vec3d(baseEntity.serverX, baseEntity.serverY, baseEntity.serverZ).subtract(serverPos);
        if (velocity.x == 0 && velocity.y == 0 && velocity.z == 0) {
            return new Input();
        }

        Pair<Input, Double> bestPossibility = null;
        for (Input input : BRUTEFORCE_INPUTS) {
            input.jumping = !baseEntity.isOnGround();
            input.sneaking = baseEntity.isSneaking();

            final boolean moving = input.movementForward != 0 || input.movementSideways != 0;
            if (velocity.horizontalLengthSquared() > 0.0 && !moving) {
                continue;
            }

            Vec3d nextPos;
            if (moving) {
                final Vec3d movementVec = MCMathUtil.toVec3D(input.getMovementInput(), false);
                nextPos = Entity.movementInputToVelocity(movementVec, 1F, (float) baseEntity.serverYaw);
            } else {
                nextPos = new Vec3d(0.0, 0.0, 0.0);
            }

            final double distance = velocity.distanceTo(nextPos);
            if (bestPossibility == null || bestPossibility.getRight() > distance) {
                bestPossibility = new Pair<>(input, distance);
            }
        }
        return bestPossibility.getLeft();
    }

}
