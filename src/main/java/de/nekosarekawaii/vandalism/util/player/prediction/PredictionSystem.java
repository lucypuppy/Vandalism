/*
 * Copyright (C) 2021-2024 Verschlxfene
 */

package de.nekosarekawaii.vandalism.util.player.prediction;

import de.nekosarekawaii.vandalism.injection.access.ILivingEntity;
import de.nekosarekawaii.vandalism.injection.access.IParticleManager;
import de.nekosarekawaii.vandalism.injection.access.ISoundSystem;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.math.MathUtil;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PredictionSystem implements MinecraftWrapper {

    /**
     * Hacky system to calculate the position of the player in the future.
     *
     * @param ticks The amount of ticks to predict
     * @return A pair of the predicted player and a list of all positions
     */
    public static Pair<ClientPlayerEntity, ArrayList<Vec3d>> predictState(final int ticks) {
        return predictState(ticks, mc.player);
    }

    /**
     * Hacky system to calculate the position of a player in the future.
     *
     * @param ticks      The amount of ticks to predict
     * @param baseEntity The player to predict
     * @return A pair of the predicted player and a list of all positions
     */
    public static Pair<ClientPlayerEntity, ArrayList<Vec3d>> predictState(final int ticks, final PlayerEntity baseEntity) {
        return predictState(ticks, baseEntity, null);
    }

    /**
     * Hacky system to calculate the position of a player in the future.
     *
     * @param ticks      The amount of ticks to predict
     * @param baseEntity The player to predict
     * @param input      The input to use
     * @return A pair of the predicted player and a list of all positions
     */
    public static Pair<ClientPlayerEntity, ArrayList<Vec3d>> predictState(final int ticks, final PlayerEntity baseEntity, final Input input) {
        return predictState(ticks, baseEntity, input, clientPlayerEntity -> false, clientPlayerEntity -> false);
    }

    /**
     * Hacky system to calculate the position of a player in the future.
     *
     * @param ticks      The amount of ticks to predict
     * @param baseEntity The player to predict
     * @param input      The input to use
     * @param abortWhen  A function that returns true if the prediction should be aborted
     * @return A pair of the predicted player and a list of all positions
     */
    public static Pair<ClientPlayerEntity, ArrayList<Vec3d>> predictState(final int ticks, final PlayerEntity baseEntity, Input input, final Function<ClientPlayerEntity, Boolean> abortWhen, final Function<ClientPlayerEntity, Boolean> abortBefore) {
        // We need to store both velocities as they are changed by the prediction
        final Vec3d selfVelocity = MathUtil.copy(baseEntity.getVelocity());
        final Vec3d localVelocity = MathUtil.copy(mc.player.getVelocity());

        // First, we need to make sure both the sound system and the particle manager are disabled
        // because otherwise the prediction will be very laggy and the player will be able to hear
        // the sounds of the prediction
        final ISoundSystem mixinSoundSystem = (ISoundSystem) mc.getSoundManager().soundSystem;
        final IParticleManager mixinParticleManager = (IParticleManager) mc.particleManager;

        final boolean wasSoundDisabled = mixinSoundSystem.vandalism$isDisabled();
        final boolean wasParticlesDisabled = mixinParticleManager.vandalism$isDisabled();

        mixinSoundSystem.vandalism$setDisabled(true);
        mixinParticleManager.vandalism$setDisabled(true);

        // We need to create a fake player instance to predict the movement
        final ClientPlayerEntity fakePlayer = NoOpClientPlayerEntity.get();

        // If the input field is null, check if the base entity is the local player and if so, use the local input
        // otherwise, calculate the input using the position updates
        if (input == null) {
            input = (baseEntity == mc.player) ? mc.player.input : getClosestInput(baseEntity);
        }

        Input input_c = input; // We don't want to break upstream code - so we copy the input
        fakePlayer.input = new Input() {

            @Override
            public void tick(boolean slowDown, float slowDownFactor) {
                // No op implementation of an input which just copies the input of the base entity
                movementForward = input_c.movementForward;
                movementSideways = input_c.movementSideways;

                pressingForward = input_c.pressingForward;
                pressingBack = input_c.pressingBack;

                pressingLeft = input_c.pressingLeft;
                pressingRight = input_c.pressingRight;

                jumping = input_c.jumping;
                sneaking = input_c.sneaking;

                if (slowDown) {
                    movementForward *= slowDownFactor;
                    movementSideways *= slowDownFactor;
                }
            }

        };

        fakePlayer.init(); // Now we need to initialize the input
        fakePlayer.copyPositionAndRotation(baseEntity); // Set position and rotation of base entity to fake player
        fakePlayer.copyFrom(baseEntity); // Copy nbt player data from base entity to fake player

        if (baseEntity == mc.player) {
            // If we are predicting the local player, we need to make sure
            // the rotations of the rotation engine are used
            // TODO Apply fake rotations here

            // Now we can set the velocity of the base entity as well as auto jump
            fakePlayer.setVelocity(baseEntity.getVelocity());
            fakePlayer.autoJumpEnabled = mc.player.isAutoJumpEnabled();
            fakePlayer.ticksToNextAutojump = mc.player.ticksToNextAutojump;
        } else {
            // If we are predicting another player, we can't know the velocity
            // or the auto jump state, so we need to set it to default values
            fakePlayer.setVelocity(MathUtil.copy(Vec3d.ZERO)); // I hate this game
            fakePlayer.autoJumpEnabled = false; // We will never know
        }

        // Now we just copy a lot of fields which are used in the tick method and for the movement to work
        fakePlayer.setOnGround(baseEntity.isOnGround());
        fakePlayer.setPose(baseEntity.getPose());
        fakePlayer.jumpingCooldown = baseEntity.jumpingCooldown;
        fakePlayer.submergedInWater = baseEntity.isSubmergedInWater();
        fakePlayer.touchingWater = baseEntity.isTouchingWater();
        fakePlayer.setSwimming(baseEntity.isSwimming());
        fakePlayer.setSprinting(baseEntity.isSprinting());
        fakePlayer.setSneaking(baseEntity.isSneaking());
        fakePlayer.verticalCollision = baseEntity.verticalCollision;
        fakePlayer.horizontalCollision = baseEntity.horizontalCollision;
        fakePlayer.collidedSoftly = baseEntity.collidedSoftly;

        final ArrayList<Vec3d> positions = new ArrayList<>();

        // Now, we can start the prediction:
        // - Reset the position since MC also does that
        // - Increase the age of the player to prevent issues in the tick method
        // - Tick the player
        // - Store the position in the list
        for (int i = 0; i < ticks; i++) {
            if (abortBefore.apply(fakePlayer)) { // This can be useful too lol
                break;
            }
            fakePlayer.resetPosition();
            fakePlayer.age++;
            fakePlayer.tick();
            positions.add(fakePlayer.getPos());
            if (abortWhen.apply(fakePlayer)) { // This can be useful if we are waiting for a specific event
                break;
            }
        }

        // At the end, we restore all previously broken fields
        mixinParticleManager.vandalism$setDisabled(wasParticlesDisabled);
        mixinSoundSystem.vandalism$setDisabled(wasSoundDisabled);

        baseEntity.setVelocity(selfVelocity);
        mc.player.setVelocity(localVelocity);

        return new Pair<>(fakePlayer, positions);
    }

    // Usually it's wrong to do that because now we are editing those elements,
    // but in this case it doesn't matter because we are editing the values we are editing
    // everytime, so it doesn't cause any issues
    private static final List<Input> POSSIBLE_INPUTS = MathUtil.possibleInputs();

    /**
     * Calculates the closest input of a player based on the velocity of the player.
     *
     * @param baseEntity The player to calculate the input for
     * @return The closest input
     */
    public static Input getClosestInput(final PlayerEntity baseEntity) {
        final Vec3d serverPos = ((ILivingEntity) baseEntity).vandalism$prevServerPos();
        if (serverPos == null) return new Input();

        final Vec3d velocity = new Vec3d(baseEntity.serverX, baseEntity.serverY, baseEntity.serverZ).subtract(serverPos);
        if (velocity.x == 0 && velocity.y == 0 && velocity.z == 0) return new Input();

        Pair<Input, Double> bestPossibility = null;
        for (Input input : POSSIBLE_INPUTS) {
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
