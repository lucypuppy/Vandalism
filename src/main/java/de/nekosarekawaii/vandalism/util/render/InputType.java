/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.util.render;

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
        FIELD_NAMES.put("NONE", GLFW.GLFW_KEY_UNKNOWN);
    }

    public static String getKeyName(final int keyCode) {
        return InputUtil.Type.KEYSYM.createFromCode(keyCode).getLocalizedText().getString();
    }

    public static boolean isPressed(final int keyCode) {
        if (keyCode == GLFW.GLFW_KEY_UNKNOWN) {
            return false;
        }
        final long handle = MinecraftClient.getInstance().getWindow().getHandle();

        return GLFW.glfwGetKey(handle, keyCode) == GLFW.GLFW_PRESS;
    }

}
