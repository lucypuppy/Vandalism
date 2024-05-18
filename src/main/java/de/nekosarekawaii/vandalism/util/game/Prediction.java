/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Prediction {

    public static LivingEntity predictEntityMovement(LivingEntity entity, int ticks, boolean predictInput) {
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
            livingEntity.tick();
        }

        return livingEntity;
    }

    public static Vec3d predictEntityPosition(LivingEntity entity, int ticks, boolean predictInput) {
        LivingEntity predictedEntity = predictEntityMovement(entity, ticks, predictInput);
        return predictedEntity.getPos();
    }

}
