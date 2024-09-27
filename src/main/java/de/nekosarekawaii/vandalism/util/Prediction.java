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

package de.nekosarekawaii.vandalism.util;

import de.nekosarekawaii.vandalism.injection.access.ILivingEntity;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class Prediction {

    // Jump/sneak states are randomized as soon as getClosestInput is called
    private static final List<Input> BRUTEFORCE_INPUTS = MathUtil.possibleInputs();

    public static LivingEntity predictEntityMovement(final LivingEntity entity, final int ticks, final boolean predictInput, final boolean predictJumping) {
        final LivingEntity livingEntity = new LivingEntity((EntityType<? extends LivingEntity>) entity.getType(), entity.getWorld()) {

            @Override
            public Iterable<ItemStack> getArmorItems() {
                return entity.getArmorItems();
            }

            @Override
            public ItemStack getEquippedStack(final EquipmentSlot slot) {
                return entity.getEquippedStack(slot);
            }

            @Override
            public void equipStack(final EquipmentSlot slot, final ItemStack stack) {
            }

            @Override
            public Arm getMainArm() {
                return entity.getMainArm();
            }

        };

        livingEntity.copyFrom(entity);
        livingEntity.copyPositionAndRotation(entity);
        livingEntity.setVelocity(entity.getVelocity());

        ClientPlayerEntity player = MinecraftWrapper.mc.player;

        final Input predictedInput = (player != null && entity.getId() == player.getId()) ? player.input : getClosestInput(entity);

        for (int i = 0; i < ticks; i++) {
            if (predictInput) {
                livingEntity.sidewaysSpeed = predictedInput.movementSideways;
                livingEntity.forwardSpeed = predictedInput.movementForward;
            }
            if (predictJumping && livingEntity.isOnGround()) {
                livingEntity.jump();
            }
            livingEntity.tick();
        }

        return livingEntity;
    }

    public static Vec3d predictEntityPosition(final LivingEntity entity, final int ticks, final boolean predictInput, final boolean predictJumping) {
        final LivingEntity predictedEntity = predictEntityMovement(entity, ticks, predictInput, predictJumping);
        return predictedEntity.getPos();
    }

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
        for (final Input input : BRUTEFORCE_INPUTS) {
            input.jumping = !baseEntity.isOnGround();
            input.sneaking = baseEntity.isSneaking();

            final boolean moving = input.movementForward != 0 || input.movementSideways != 0;
            if (velocity.horizontalLengthSquared() > 0.0 && !moving) {
                continue;
            }

            Vec3d nextPos;
            if (moving) {
                final Vec3d movementVec = MathUtil.toVec3D(input.getMovementInput(), false);
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
