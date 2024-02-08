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

package de.nekosarekawaii.vandalism.util.game;

import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MovementUtil implements MinecraftWrapper {

    private static final float[] POSSIBLE_MOVEMENTS = new float[]{-1f, 0.0f, 1f};

    /**
     * Get the direction of in which the player is looking.
     *
     * @return The direction in which the player is looking.
     */
    public static double getDirection() {
        return getDirection(true);
    }

    /**
     * Get the direction of in which the player is looking.
     *
     * @param directionOffset The offset to use.
     *
     * @return The direction in which the player is looking.
     */
    public static double getDirection(final float directionOffset) {
        return getDirection(directionOffset, true);
    }

    /**
     * Get the direction of in which the player is looking.
     *
     * @param movingCheck If the player is moving.
     * @return The direction in which the player is looking.
     */
    public static double getDirection(final boolean movingCheck) {
        return getDirection(0, movingCheck);
    }

    /**
     * Get the direction of in which the player is looking.
     *
     * @param directionOffset The offset to use.
     * @param movingCheck If the player is moving.
     *
     * @return The direction in which the player is looking.
     */
    public static double getDirection(final float directionOffset, final boolean movingCheck) {
        if (mc.player == null) return 0;
        if (movingCheck && !isMoving()) return Math.toRadians(mc.player.getYaw() + 90.0f);
        final float offset = (180.0f + directionOffset);
        return (Math.atan2(mc.player.forwardSpeed, mc.player.sidewaysSpeed) / Math.PI * offset + mc.player.getYaw()) * Math.PI / offset;
    }

    /**
     * Set the speed of the player.
     *
     * @param speed The speed to set.
     * @return The new velocity of the player.
     */
    public static Vec3d setSpeed(final double speed) {
        return setSpeed(speed, 0);
    }

    /**
     * Set the speed of the player.
     *
     * @param speed The speed to set.
     * @param offset The offset to use.
     * @return The new velocity of the player.
     */
    public static Vec3d setSpeed(final double speed, final float offset) {
        return setSpeed(mc.player, speed, offset);
    }

    /**
     * Set the speed of an entity.
     *
     * @param entity The entity to set the speed of.
     * @param speed The speed to set.
     * @return The new velocity of the entity.
     */
    public static Vec3d setSpeed(final Entity entity, final double speed) {
        return setSpeed(entity, speed, 0);
    }

    /**
     * Set the speed of an entity.
     *
     * @param entity The entity to set the speed of.
     * @param speed The speed to set.
     * @param offset The offset to use.
     * @return The new velocity of the entity.
     */
    public static Vec3d setSpeed(final Entity entity, final double speed, final float offset) {
        final double direction = getDirection(offset);
        entity.setVelocity(Math.cos(direction) * speed, entity.getVelocity().getY(), Math.sin(direction) * speed);
        return entity.getVelocity();
    }

    /**
     * Check if the player is moving.
     *
     * @return If the player is moving.
     */
    public static boolean isMoving() {
        if (mc.player == null) return false;
        return mc.player.forwardSpeed != 0 || mc.player.sidewaysSpeed != 0;
    }

    public static Vec3d applyFriction(final Vec3d velocity, final float percentage) {
        if (mc.player == null) return Vec3d.ZERO;
        final BlockPos blockPos = mc.player.getVelocityAffectingPos();
        final float p = mc.world.getBlockState(blockPos).getBlock().getSlipperiness();
        float baseValue = 0.91f;
        float percentageFactor = percentage / 100f;
        float f = mc.player.isOnGround()
                ? 1.0f - percentageFactor * (1.0f - p)
                : 1.0f - percentageFactor * (1.0f - baseValue);
        return velocity.multiply(f, 1, f);
    }

    /**
     * Get the base speed of a player.
     *
     * @return The base speed.
     */
    public static double getBaseSpeed() {
        if (mc.player == null) return 0;

        double baseSpeed = 0.153d;
        if (!mc.player.isSprinting()) {
            baseSpeed = 0.118d;
        }

        if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            final int amplifier = 1 + mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
            final double fixedSpeedMotion = 0.11d;
            baseSpeed *= 1.0d + fixedSpeedMotion * (amplifier + 1);
        }

        return baseSpeed;
    }

    /**
     * Get the speed of the player.
     * @return The speed of the player.
     */
    public static double getSpeed() {
        if (mc.player == null) return 0;
        return Math.hypot(mc.player.getVelocity().getX(), mc.player.getVelocity().getZ());
    }

    /**
     * Clip the player.
     * @param vertical The vertical value.
     * @param horizontal The horizontal value.
     */
    public static void clip(final double vertical, final double horizontal) {
        if (mc.player == null) return;
        final double direction = getDirection(false);
        mc.player.setPos(mc.player.getX() - Math.sin(direction) * horizontal, mc.player.getY() + vertical, mc.player.getZ() + Math.cos(direction) * horizontal);
    }

    /**
     * Get the fixed move inputs.
     * @param yaw The yaw to use.
     * @return The fixed move inputs.
     */
    public static float[] getFixedMoveInputs(final float yaw) {
        final float[] inputs = new float[2];
        if (Math.abs(mc.player.forwardSpeed) > 0f || Math.abs(mc.player.sidewaysSpeed) > 0f) {
            final float wantedYaw = getInputAngle(mc.player.getYaw());
            Vec3d movementInput;
            final float currentDX = MathHelper.sin((float) Math.toRadians(yaw));
            final float currentDZ = MathHelper.cos((float) Math.toRadians(yaw));
            //as seen here
            float currentBestForward = 1.0f;
            float currentBestStrafing = 0.0f;
            //the current best difference between any found combination and wantedYaw,
            //initialized to be 180 because that's the maximum value a difference can
            //be after being passed through MathHelper::wrapAngleToA18_float
            float currentBestDiff = Float.MAX_VALUE;
            //use this and not just hardcode 0.98f, because moveForward and moveStrafing
            //is also dependent on whether the player is sneaking or using an item
            final float mag = Math.max(Math.abs(mc.player.forwardSpeed), Math.abs(mc.player.sidewaysSpeed));
            //loop through all possible combinations of player.moveForward and player.moveStrafing
            for (final float forward : POSSIBLE_MOVEMENTS) {
                for (final float strafing : POSSIBLE_MOVEMENTS) {
                    //don't do anything when the combination would make the player stand still
                    //(this would mess sin and cos)
                    if (forward == 0.0f && strafing == 0.0f) continue;
                    movementInput = new Vec3d(forward, mc.player.upwardSpeed, strafing);
                    //Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply((double)speed);
                    //motionX and motionZ the player would have with
                    //the current combination of moveForward and moveStrafing
                    //vec3d.x * (double)g - vec3d.z * (double)f
                    final float mX = (float) (movementInput.x * currentDZ - movementInput.z * currentDX);
                    //vec3d.z * (double)g + vec3d.x * (double)f
                    final float mZ = (float) (movementInput.z * currentDZ + movementInput.x * currentDX);
                    //the yaw angle relative to the players rotation this
                    //motion would make the player walk towards
                    final float angle = (float) (Math.atan2(mZ, mX) * 180.0d / Math.PI - 90.0f);
                    //and the difference of it to the wanted yaw relative to the player
                    final float diff = Math.abs(MathHelper.wrapDegrees(angle - wantedYaw));
                    //set combination, if signed distance between
                    //wantedYaw and angle is lower than the last best difference
                    if (diff < currentBestDiff) {
                        currentBestForward = forward;
                        currentBestStrafing = strafing;
                        currentBestDiff = diff;
                    }
                }
            }
            inputs[0] = (currentBestForward * mag);
            inputs[1] = (currentBestStrafing * mag);
        }
        return inputs;
    }

    /**
     * Get the input angle.
     * @param yaw The yaw to use.
     * @return The input angle.
     */
    public static float getInputAngle(final float yaw) {
        final float vertical = mc.player.forwardSpeed;
        final float horizontal = mc.player.sidewaysSpeed;
        if (vertical > 0) {
            if (horizontal > 0) return yaw - 45f;
            else if (horizontal < 0) return yaw + 45f;
            else return yaw;
        } else if (vertical < 0) {
            if (horizontal > 0) return yaw - 135f;
            else if (horizontal < 0) return yaw + 135f;
            else return MathHelper.wrapDegrees(yaw - 180f);
        }

        if (horizontal > 0) return yaw - 90f;
        else if (horizontal < 0) return yaw + 90f;

        return yaw;
    }

}
