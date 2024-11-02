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

package de.nekosarekawaii.vandalism.util;

// Author: Recyz
public class ControlChars {

    public static char ESC = 0x1B;
    public static char BELL = 0x07;
    public static char CSI = 0x9B;
    public static String e_screen_filler = ESC + "#8";
    public static String enchantment_table = ESC + "(0";
    public static String clear_screen = ESC + "[2J";
    public static String input_1 = ESC + "Z";
    public static String input_2 = ESC + "[c";
    public static String input_3 = ESC + "[6n";
    public static String blink = ESC + "[5m";
    public static String black_foreground = ESC + "[30m";
    public static String screen_reset = ESC + "c";
    public static String freeze_screen = ESC + "^";

    public static String customMessage(String msg) {
        return ESC + "^" + msg + ESC + "\\";
    }

}