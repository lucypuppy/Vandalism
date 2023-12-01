package de.vandalismdevelopment.vandalism.util.minecraft;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;

public class MinecraftUtil {

    protected static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    protected static ClientPlayerEntity player() {
        return mc().player;
    }

    protected static ClientWorld world() {
        return mc().world;
    }

    protected static ClientPlayNetworkHandler networkHandler() {
        return mc().getNetworkHandler();
    }

    protected static ClientPlayerInteractionManager interactionManager() {
        return mc().interactionManager;
    }

    protected static Screen currentScreen() {
        return mc().currentScreen;
    }

}
