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

import org.apache.commons.lang3.time.CalendarUtils;

public class DateUtil {

    public static int getDay() {
        return CalendarUtils.getInstance().getDayOfMonth();
    }

    public static int getMonth() {
        return CalendarUtils.getInstance().getMonth();
    }

    public static int getYear() {
        return CalendarUtils.getInstance().getYear();
    }

    public static boolean isBirthday() {
        return getDay() == 2 && getMonth() == 7;
    }

    public static boolean isAprilFools() {
        return getDay() == 1 && getMonth() == 3;
    }

    public static boolean isHalloween() {
        return getDay() == 31 && getMonth() == 10;
    }

    public static boolean isChristmas() {
        return getDay() == 24 && getMonth() == 12;
    }

    public static boolean isNewYear() {
        return getDay() == 31 && getMonth() == 12;
    }

    public static boolean isValentinesDay() {
        return getDay() == 14 && getMonth() == 2;
    }

    public static boolean isEaster() {
        return getDay() == 4 && getMonth() == 4;
    }

}
