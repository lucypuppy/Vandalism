package de.nekosarekawaii.foxglove.gui.imgui;

import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.MinecraftClient;

public class ImGuiUtil {

    public static int getInGameFlags(int windowFlags) {
        windowFlags |= ImGuiWindowFlags.NoCollapse;

        if (MinecraftClient.getInstance().mouse.isCursorLocked()) {
            windowFlags |= ImGuiWindowFlags.NoTitleBar;
            windowFlags |= ImGuiWindowFlags.NoBackground;
            windowFlags |= ImGuiWindowFlags.NoMove;
            windowFlags |= ImGuiWindowFlags.NoResize;
            windowFlags |= ImGuiWindowFlags.NoMouseInputs;
            windowFlags |= ImGuiWindowFlags.NoScrollWithMouse;
        }

        return windowFlags;
    }

}
