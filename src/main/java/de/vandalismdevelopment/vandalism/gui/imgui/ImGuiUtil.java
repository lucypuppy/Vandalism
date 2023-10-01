package de.vandalismdevelopment.vandalism.gui.imgui;

import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.MinecraftClient;

public class ImGuiUtil {

    public static int getInGameFlags(int windowFlags) {
        windowFlags |= ImGuiWindowFlags.NoCollapse;

        if (MinecraftClient.getInstance().mouse.isCursorLocked()) {
            windowFlags |= ImGuiWindowFlags.NoTitleBar;
            windowFlags |= ImGuiWindowFlags.NoBackground;
            windowFlags |= ImGuiWindowFlags.NoScrollbar;

            windowFlags |= ImGuiWindowFlags.NoMove;
            windowFlags |= ImGuiWindowFlags.NoResize;
            windowFlags |= ImGuiWindowFlags.NoInputs;
            windowFlags |= ImGuiWindowFlags.NoScrollWithMouse;
        }

        return windowFlags;
    }

}
