package de.vandalismdevelopment.vandalism.util.minecraft;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.LocalDate;
import java.time.Month;

public interface MinecraftWrapper {

    default MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    default ClientPlayerEntity player() {
        if (this.mc() == null) {
            return null;
        }
        return this.mc().player;
    }

    default ClientWorld world() {
        if (this.mc() == null) {
            return null;
        }
        return this.mc().world;
    }

    default ClientPlayerInteractionManager interactionManager() {
        if (this.mc() == null) {
            return null;
        }
        return this.mc().interactionManager;
    }

    default ClientPlayNetworkHandler networkHandler() {
        if (this.mc() == null) {
            return null;
        }
        return this.mc().getNetworkHandler();
    }

    default TextRenderer textRenderer() {
        if (this.mc() == null) {
            return null;
        }
        return this.mc().textRenderer;
    }

    default Screen currentScreen() {
        if (this.mc() == null) {
            return null;
        }
        return this.mc().currentScreen;
    }

    default void setScreen(@Nullable final Screen screen) {
        if (this.mc() == null) {
            return;
        }
        this.mc().setScreen(screen);
    }

    default GameOptions options() {
        if (this.mc() == null) {
            return null;
        }
        return this.mc().options;
    }

    default Keyboard keyboard() {
        if (this.mc() == null) {
            return null;
        }
        return this.mc().keyboard;
    }

    default Mouse mouse() {
        if (this.mc() == null) {
            return null;
        }
        return this.mc().mouse;
    }

    default Window window() {
        if (this.mc() == null) {
            return null;
        }
        return this.mc().getWindow();
    }

    default boolean isTrollTime() {
        return Math.random() < 0.02;
    }

    default boolean isHalloween() {
        return LocalDate.now().getMonth() == Month.OCTOBER && LocalDate.now().getDayOfMonth() == 31;
    }

    default boolean isAprilFools() {
        return LocalDate.now().getMonth() == Month.APRIL && LocalDate.now().getDayOfMonth() == 1;
    }

}
