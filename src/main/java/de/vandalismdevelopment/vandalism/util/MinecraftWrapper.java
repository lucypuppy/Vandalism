package de.vandalismdevelopment.vandalism.util;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;

public interface MinecraftWrapper {

    default MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    default ClientPlayerEntity player() {
        return MinecraftClient.getInstance().player;
    }

    default ClientWorld world() {
        return MinecraftClient.getInstance().world;
    }

    default ClientPlayerInteractionManager interactionManager() {
        return MinecraftClient.getInstance().interactionManager;
    }

    default ClientPlayNetworkHandler networkHandler() {
        return MinecraftClient.getInstance().getNetworkHandler();
    }

    default TextRenderer textRenderer() {
        return MinecraftClient.getInstance().textRenderer;
    }

    default Screen currentScreen() {
        return MinecraftClient.getInstance().currentScreen;
    }

    default void setScreen(@Nullable final Screen screen) {
        MinecraftClient.getInstance().setScreen(screen);
    }

    default GameOptions options() {
        return MinecraftClient.getInstance().options;
    }

    default Keyboard keyboard() {
        return MinecraftClient.getInstance().keyboard;
    }

    default Mouse mouse() {
        return MinecraftClient.getInstance().mouse;
    }

}
