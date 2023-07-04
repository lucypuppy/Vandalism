package me.nekosarekawaii.foxglove.wrapper;

import net.minecraft.client.MinecraftClient;

public interface MinecraftWrapper {


    default MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    default boolean isInGame() {
        return MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null;
    }

}
