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

import de.nekosarekawaii.vandalism.event.player.StrafeListener;
import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MovementUtil implements MinecraftWrapper {

    /**
     * Get the direction of in which the player is looking.
     *
     * @return The direction in which the player is looking.
     */
    public static double getDirection() {
        return getDirection(mc.player.getYaw());
    }

    /**
     * Get the direction of in which the player is looking.
     *
     * @param rotationYaw The rotation yaw to use.
     * @return The direction in which the player is looking.
     */
    public static double getDirection(float rotationYaw) {
        final float moveForward = mc.player.forwardSpeed;
        final float moveStrafing = mc.player.sidewaysSpeed;

        if (moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (moveForward < 0F) forward = -0.5F;
        else if (moveForward > 0F) forward = 0.5F;

        if (moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    /**
     * Set the speed of the player.
     *
     * @param speed The speed to set.
     * @return The new velocity of the player.
     */
    public static Vec3d setSpeed(final double speed) {
        return setSpeed(mc.player, speed, mc.player.getYaw());
    }

    /**
     * Set the speed of the player.
     *
     * @param speed  The speed to set.
     * @param offset The offset to use.
     * @return The new velocity of the player.
     */
    public static Vec3d setSpeed(final double speed, final float offset) {
        return setSpeed(mc.player, speed, mc.player.getYaw() + offset);
    }

    /**
     * Set the speed of an entity.
     *
     * @param entity The entity to set the speed of.
     * @param speed  The speed to set.
     * @param yaw    The yaw to use.
     * @return The new velocity of the entity.
     */
    public static Vec3d setSpeed(final Entity entity, final double speed, final float yaw) {
        final double direction = getDirection(yaw);
        entity.setVelocity(-Math.sin(direction) * speed, entity.getVelocity().getY(), Math.cos(direction) * speed);
        return entity.getVelocity();
    }

    /**
     * Check if the player is moving forwards.
     *
     * @return If the player is moving forwards.
     */
    public static boolean isMovingForwards() {
        if (mc.player == null) return false;
        return mc.player.forwardSpeed > 0;
    }

    /**
     * Check if the player is moving backwards.
     *
     * @return If the player is moving backwards.
     */
    public static boolean isMovingBackwards() {
        if (mc.player == null) return false;
        return mc.player.forwardSpeed < 0;
    }

    /**
     * Check if the player is moving sideways.
     *
     * @return If the player is moving sideways.
     */
    public static boolean isMovingSideways() {
        if (mc.player == null) return false;
        return mc.player.sidewaysSpeed != 0;
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

    /**
     * Get the player's velocity.
     *
     * @return The player's velocity.
     */
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
     *
     * @return The speed of the player.
     */
    public static double getSpeed() {
        if (mc.player == null) return 0;
        return Math.hypot(mc.player.getVelocity().getX(), mc.player.getVelocity().getZ());
    }

    /**
     * Clip the player.
     *
     * @param vertical   The vertical value.
     * @param horizontal The horizontal value.
     */
    public static void clip(final double direction, final double vertical, final double horizontal) {
        if (mc.player == null) return;
        mc.player.setPos(mc.player.getX() - Math.sin(direction) * horizontal, mc.player.getY() + vertical, mc.player.getZ() + Math.cos(direction) * horizontal);
    }

    /**
     * Clip the player.
     *
     * @param vertical   The vertical value.
     * @param horizontal The horizontal value.
     */

    public static void clip(final double vertical, final double horizontal) {
        if (mc.player == null) return;
        final double direction = Math.toRadians(mc.player.getYaw());
        clip(direction, vertical, horizontal);
    }

    /**
     * Clip the player but bypass the clip checks.
     *
     * @param startX The start x to use.
     * @param startY The start y to use.
     * @param startZ The start z to use.
     * @param endX   The end x to use.
     * @param endY   The end y to use.
     * @param endZ   The end z to use.
     */
    public static void bypassClip(final double startX, final double startY, final double startZ, final double endX, final double endY, final double endZ) {
        final Vec3d start = new Vec3d(startX, startY, startZ);
        final Vec3d end = new Vec3d(endX, endY, endZ);
        final double distance = start.distanceTo(end);
        final int packetsRequired = (int) Math.ceil(Math.abs(distance / 10));
        for (int packetNumber = 0; packetNumber < (packetsRequired - 1); packetNumber++) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
        }
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(end.x, end.y, end.z, true));
        mc.player.setPos(end.x, end.y, end.z);
    }

    /**
     * Clip the player but bypass the clip checks.
     *
     * @param horizontal The horizontal to use.
     * @param vertical   The vertical to use.
     */
    public static void bypassClip(final double horizontal, final double vertical) {
        if (mc.player == null) return;
        final double currentX = mc.player.getX();
        final double currentY = mc.player.getY();
        final double currentZ = mc.player.getZ();
        final double direction = Math.toRadians(mc.player.getYaw());
        bypassClip(currentX, currentY, currentZ, currentX - Math.sin(direction) * horizontal, currentY + vertical, currentZ + Math.cos(direction) * horizontal);
    }

    /**
     * Get the input angle.
     *
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

    /**
     * Get the speed related to the yaw.
     *
     * @param yaw The yaw to use.
     * @return The speed related to the yaw.
     */
    public static double getSpeedRelatedToYaw(float yaw) {
        yaw = MathHelper.wrapDegrees(yaw);

        final float angle = MathHelper.wrapDegrees((float) (Math.atan2(mc.player.getVelocity().getZ(), mc.player.getVelocity().getX()) * 180.0d / Math.PI - 90.0f));
        final double diff = Math.abs(MathHelper.wrapDegrees(angle - yaw));

        if (diff < 80) {
            final double yawRadians = Math.toRadians(yaw);
            final Vec3d directionVector = new Vec3d(-Math.sin(yawRadians), 0, Math.cos(yawRadians));
            final Vec3d playerVelocity = new Vec3d(mc.player.getVelocity().getX(), 0, mc.player.getVelocity().getZ());
            return playerVelocity.normalize().dotProduct(directionVector);
        }

        return -1.0;
    }

    /**
     * Get the speed related to the yaw.
     * @param posY The position y to use.
     * @return The speed related to the yaw.
     */
    public static double roundToGround(final double posY) {
        return Math.round(posY / MinecraftConstants.MAGIC_ON_GROUND_MODULO_FACTOR) * MinecraftConstants.MAGIC_ON_GROUND_MODULO_FACTOR;
    }

    /**
     * Get the speed related to the yaw.
     *
     * @param rotation The rotation to use.
     * @param event    The strafe event to use.
     * @return The speed related to the yaw.
     */
    public static Vec3d silentMoveFix(Rotation rotation, StrafeListener.StrafeEvent event) {
        float currentMotionX = (float) (event.movementInput.x * Math.cos(Math.toRadians(event.yaw)) - event.movementInput.z * Math.sin(Math.toRadians(event.yaw)));
        float currentMotionZ = (float) (event.movementInput.z * Math.cos(Math.toRadians(event.yaw)) + event.movementInput.x * Math.sin(Math.toRadians(event.yaw)));

        float bestForward = (float) event.movementInput.z;
        float bestStrafing = (float) event.movementInput.x;
        float bestDist = -1;

        float[] possibleInputs = new float[]{-1.0F, 0.0F, 1.0F};
        for (float forward : possibleInputs) {
            for (float strafing : possibleInputs) {
                if (forward == 0.0F && strafing == 0.0F) continue;

                float motionX = (float) (strafing * Math.cos(Math.toRadians(rotation.getYaw())) - forward * Math.sin(Math.toRadians(rotation.getYaw())));
                float motionZ = (float) (forward * Math.cos(Math.toRadians(rotation.getYaw())) + strafing * Math.sin(Math.toRadians(rotation.getYaw())));

                float deltaX = motionX - currentMotionX;
                float deltaZ = motionZ - currentMotionZ;

                float dist = (float) Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
                if (bestDist == -1 || dist < bestDist) {
                    bestForward = forward;
                    bestStrafing = strafing;
                    bestDist = dist;
                }
            }
        }

        float mag = (float) Math.max(Math.abs(event.movementInput.z), Math.abs(event.movementInput.x));
        return new Vec3d(bestStrafing * mag, event.movementInput.y, bestForward * mag);
    }

}
