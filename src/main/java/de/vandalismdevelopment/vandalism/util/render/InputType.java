package de.vandalismdevelopment.vandalism.util.render;

import net.lenni0451.reflect.stream.RStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class InputType {
    public static final Map<String, Integer> FIELD_NAMES = new HashMap<>();

    static {
        RStream.of(InputUtil.class).fields().filter(field -> field.name().startsWith("GLFW_KEY_")).forEach(key -> {
            final int keyCode = key.get();
            FIELD_NAMES.put(getKeyName(keyCode), keyCode);
        });
    }

    public static String getKeyName(final int keyCode) {
        return InputUtil.Type.KEYSYM.createFromCode(keyCode).getLocalizedText().getString();
    }

    public static boolean isPressed(final int keyCode) {
        final long handle = MinecraftClient.getInstance().getWindow().getHandle();

        return GLFW.glfwGetKey(handle, keyCode) == GLFW.GLFW_PRESS;
    }

}
