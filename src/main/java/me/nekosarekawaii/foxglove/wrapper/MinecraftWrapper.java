package me.nekosarekawaii.foxglove.wrapper;

import net.minecraft.client.MinecraftClient;

/**
 * The MinecraftWrapper interface provides convenient access to the Minecraft client instance and related functionality.
 */
public interface MinecraftWrapper {

    /**
     * Retrieves the Minecraft client instance.
     *
     * @return The Minecraft client instance.
     */
    default MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    /**
     * Checks if the player is currently in the game.
     *
     * @return {@code true} if the player is in the game (the player and world are not null); {@code false} otherwise.
     */
    default boolean isInGame() {
        return MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null;
    }

}
