/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

public class GameNotifications {

    public static final SystemToast.Type MOD_TOAST_ID = new SystemToast.Type();

    public static void simple(Text title, Text message) {
        MinecraftClient.getInstance().getToastManager().add(new SystemToast(MOD_TOAST_ID, title, message));
    }

    public static void simple(String title, Text message) {
        simple(Text.literal(title), message);
    }

    public static void simple(Text title, String message) {
        simple(title, Text.literal(message));
    }

    public static void simple(String title, String message) {
        simple(Text.literal(title), Text.literal(message));
    }

    public static void multiline(Text title, Text message) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        mc.getToastManager().add(SystemToast.create(mc, MOD_TOAST_ID, title, message));
    }

    public static void multiline(String title, Text message) {
        multiline(Text.literal(title), message);
    }

    public static void multiline(Text title, String message) {
        multiline(title, Text.literal(message));
    }

    public static void multiline(String title, String message) {
        multiline(Text.literal(title), Text.literal(message));
    }
}
