/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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
