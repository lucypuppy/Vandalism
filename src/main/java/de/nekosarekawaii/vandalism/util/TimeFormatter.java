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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class contains some useful methods for formatting time.
 *
 * @see DateTimeFormatter
 */
public final class TimeFormatter {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * @return The current date in the format dd.MM.yyyy
     */
    public static String currentDate() {
        return DATE_FORMAT.format(LocalDateTime.now());
    }

    /**
     * @return The current time in the format HH:mm:ss
     */
    public static String currentTime() {
        return TIME_FORMAT.format(LocalDateTime.now());
    }

    /**
     * @return The current date and time in the format dd.MM.yyyy HH:mm:ss
     */
    public static String currentDateTime() {
        return currentDate() + " " + currentTime();
    }

    /**
     * @param time The time to format
     * @return The given time in the format dd.MM.yyyy
     */
    public static String formatTime(final LocalDateTime time) {
        return TIME_FORMAT.format(time);
    }

    /**
     * @param time The time to format
     * @return The given time in the format HH:mm:ss
     */
    public static String formatDate(final LocalDateTime time) {
        return DATE_FORMAT.format(time);
    }

    /**
     * @param time The time to format
     * @return The given time in the format dd.MM.yyyy HH:mm:ss
     */
    public static String formatDateTime(final LocalDateTime time) {
        return formatDate(time) + " " + formatTime(time);
    }

}