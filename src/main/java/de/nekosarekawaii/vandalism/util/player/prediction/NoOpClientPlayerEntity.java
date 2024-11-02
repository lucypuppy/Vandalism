/*
 * Copyright (C) 2021-2024 Verschlxfene
 */

package de.nekosarekawaii.vandalism.util.player.prediction;

import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.math.BlockPos;

/**
 * Implementation of an empty player entity. This is used to create a fake player instance.
 * See {@link NoOpClientPlayNetworkHandler} and {@link PredictionSystem} for more information.
 */
public class NoOpClientPlayerEntity extends ClientPlayerEntity implements MinecraftWrapper {

    /**
     * Creates a new fake player instance.
     *
     * @return The new instance.
     * @throws IllegalStateException If the world is not loaded.
     */
    public static NoOpClientPlayerEntity get() {
        if (mc.player == null || mc.world == null) {
            throw new IllegalStateException("Fake player can only be created when a world is loaded");
        }
        return new NoOpClientPlayerEntity();
    }

    private NoOpClientPlayerEntity() {
        super(mc, mc.world, NoOpClientPlayNetworkHandler.get(), new StatHandler(), new ClientRecipeBook(), false, false);
    }

    @Override
    public float getHealth() {
        return getMaxHealth(); // We are god
    }

    @Override
    public void tickMovement() {
        fallDistance = 0F;
        super.tickMovement();
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    @Override
    protected boolean isCamera() {
        return true;
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
    }

    @Override
    public void playSoundToPlayer(SoundEvent sound, SoundCategory category, float volume, float pitch) {
    }
}
