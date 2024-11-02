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

import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

/**
 * Don't use this class, it's just a shitty way to show notifications in the game.
 */
public class ShittyGameNotifications implements MinecraftWrapper {

    public static final SystemToast.Type MOD_TOAST_ID = new SystemToast.Type();

    public static void simple(final Text title, final Text message) {
        mc.getToastManager().add(new SystemToast(MOD_TOAST_ID, title, message));
    }

    public static void simple(final String title, final Text message) {
        simple(Text.literal(title), message);
    }

    public static void simple(final Text title, final String message) {
        simple(title, Text.literal(message));
    }

    public static void simple(final String title, final String message) {
        simple(Text.literal(title), Text.literal(message));
    }

    public static void multiline(final Text title, final Text message) {
        mc.getToastManager().add(SystemToast.create(mc, MOD_TOAST_ID, title, message));
    }

    public static void multiline(final String title, final Text message) {
        multiline(Text.literal(title), message);
    }

    public static void multiline(final Text title, final String message) {
        multiline(title, Text.literal(message));
    }

    public static void multiline(final String title, final String message) {
        multiline(Text.literal(title), Text.literal(message));
    }

}
