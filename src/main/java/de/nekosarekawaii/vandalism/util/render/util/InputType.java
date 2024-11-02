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

package de.nekosarekawaii.vandalism.util.render.util;

import net.lenni0451.reflect.stream.RStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputType {

    public static final Map<String, Integer> FIELD_NAMES = new HashMap<>();

    private static final List<Integer> BUTTONS = new ArrayList<>();

    private static final List<Integer> DENIED_BUTTONS = List.of(
            GLFW.GLFW_MOUSE_BUTTON_LEFT,
            GLFW.GLFW_MOUSE_BUTTON_LAST
    );

    static {
        RStream.of(GLFW.class).fields().filter(field -> field.name().startsWith("GLFW_KEY_") && !field.name().equals("GLFW_KEY_LAST")).forEach(key -> {
            final int keyCode = key.get();
            FIELD_NAMES.put(getName(keyCode).toUpperCase(), keyCode);
        });
        RStream.of(GLFW.class).fields().filter(field -> field.name().startsWith("GLFW_MOUSE_BUTTON_")).forEach(button -> {
            final int buttonCode = button.get();
            if (!DENIED_BUTTONS.contains(buttonCode)) {
                BUTTONS.add(buttonCode);
                FIELD_NAMES.put(getName(buttonCode).toUpperCase(), buttonCode);
            }
        });
        FIELD_NAMES.put("NONE", GLFW.GLFW_KEY_UNKNOWN);
        FIELD_NAMES.put("UNKNOWN", GLFW.GLFW_KEY_UNKNOWN);
    }

    public static String getName(int code) {
        if (BUTTONS.contains(code)) {
            return InputUtil.Type.MOUSE.createFromCode(code).getLocalizedText().getString();
        }
        if (DENIED_BUTTONS.contains(code)) {
            code = GLFW.GLFW_KEY_UNKNOWN;
        }
        if (code == GLFW.GLFW_KEY_UNKNOWN) {
            return "Unknown";
        }
        return InputUtil.Type.KEYSYM.createFromCode(code).getLocalizedText().getString();
    }

    public static boolean isAction(final int code, final int action) {
        if (code == GLFW.GLFW_KEY_UNKNOWN || DENIED_BUTTONS.contains(code)) {
            return false;
        }

        if (BUTTONS.contains(code)) {
            return GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), code) == action;
        } else {
            return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), code) == action;
        }
    }

    public static boolean isPressed(final int code) {
        return isAction(code, GLFW.GLFW_PRESS);
    }

    public static boolean isReleased(final int code) {
        return isAction(code, GLFW.GLFW_RELEASE);
    }

}
