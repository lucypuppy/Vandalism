package de.vandalismdevelopment.vandalism.util.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public enum InputType {

    KEYBOARD,
    MOUSE;

    public InputUtil.Type toGame() {
        return switch (this) {
            case KEYBOARD -> InputUtil.Type.KEYSYM;
            case MOUSE -> InputUtil.Type.MOUSE;
        };
    }

    public String getKeyName(final int keyCode) {
        return toGame().createFromCode(keyCode).getLocalizedText().getString();
    }

    public boolean isPressed(final int keyCode) {
        final long handle = MinecraftClient.getInstance().getWindow().getHandle();

        if (this == KEYBOARD) {
            return GLFW.glfwGetKey(handle, keyCode) == GLFW.GLFW_PRESS;
        } else {
            return GLFW.glfwGetMouseButton(handle, keyCode) == GLFW.GLFW_PRESS;
        }
    }

    public static InputType fromGame(final InputUtil.Type type) {
        return switch (type) {
            case KEYSYM, SCANCODE -> KEYBOARD;
            case MOUSE -> MOUSE;
        };
    }

}
