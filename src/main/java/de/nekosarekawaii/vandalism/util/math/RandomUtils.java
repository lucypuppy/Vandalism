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

package de.nekosarekawaii.vandalism.util.math;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class contains some useful methods for generating random numbers.
 */
public final class RandomUtils {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * @param minIndex The min index.
     * @param length   The length of the array or list or something similar.
     * @return A random int from min index to max index (length - 1)
     */
    public static int randomIndex(final int minIndex, final int length) {
        if (minIndex >= length) return minIndex;
        return ThreadLocalRandom.current().nextInt(minIndex, length);
    }

    /**
     * @param length The length of the array or list or something similar.
     * @return A random int from 0 to max index (length - 1)
     */
    public static int randomIndex(final int length) {
        return randomIndex(0, length);
    }

    /**
     * @param min The min value.
     * @param max The max value.
     * @return A random integer between min and max.
     */
    public static int randomInt(final int min, final int max) {
        if (min >= max) return min;
        return ThreadLocalRandom.current().nextInt(min, max + 1); // + 1 because the returned value could never reach the max value
    }

    /**
     * @param max The max value.
     * @return A random integer between 0 and max.
     */
    public static int randomInt(final int max) {
        return randomInt(0, max);
    }

    /**
     * @param min The min value.
     * @param max The max value.
     * @return A random double between min and max.
     */
    public static double randomDouble(final double min, final double max) {
        if (min >= max) return min;
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    /**
     * @param min The min value.
     * @param max The max value.
     * @return A random float between min and max.
     */
    public static float randomFloat(final float min, final float max) {
        if (min >= max) return min;
        return min + ThreadLocalRandom.current().nextFloat() * (max - min); // Java 8 support
    }

    /**
     * @param min The min value.
     * @param max The max value.
     * @return A random long between min and max.
     */
    public static long randomLong(final long min, final long max) {
        if (min >= max) return min;
        return ThreadLocalRandom.current().nextLong(min, max);
    }

    /**
     * @param min The min value.
     * @param max The max value.
     * @return A random short between min and max.
     */
    public static short randomShort(final short min, final short max) {
        if (min >= max) return min;
        return (short) ThreadLocalRandom.current().nextInt(min, max);
    }

    /**
     * @param min The min value.
     * @param max The max value.
     * @return A random byte between min and max.
     */
    public static byte randomByte(final byte min, final byte max) {
        if (min >= max) return min;
        return (byte) ThreadLocalRandom.current().nextInt(min, max);
    }

    /**
     * @param min The min value.
     * @param max The max value.
     * @return A random char between min and max.
     */
    public static char randomChar(final char min, final char max) {
        if (min >= max) return min;
        return (char) ThreadLocalRandom.current().nextInt(min, max);
    }

    /**
     * @return A random boolean (either true or false).
     */
    public static boolean randomBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    /**
     * @param array The array to get the random element from.
     * @param <T>   The type of the array.
     * @return A random element from the given array.
     */
    public static <T> T randomElement(final T[] array) {
        if (array.length == 0) return null;
        return array[ThreadLocalRandom.current().nextInt(0, array.length)];
    }

    /**
     * @param list The list to get the random element from.
     * @param <T>  The type of the list.
     * @return A random element from the given list.
     */
    public static <T> T randomElement(final List<T> list) {
        if (list.isEmpty()) return null;
        return list.get(ThreadLocalRandom.current().nextInt(0, list.size()));
    }

    /**
     * @param length The length of the byte array.
     * @return A random byte array with the specified length.
     */
    public static byte[] randomBytes(final int length) {
        return randomBytes(length, length);
    }

    /**
     * @param minLength The min length of the byte array.
     * @param maxLength The max length of the byte array.
     * @return A random byte array with a random length between minLength and maxLength.
     */
    public static byte[] randomBytes(final int minLength, final int maxLength) {
        final byte[] bytes = new byte[randomIndex(minLength, maxLength)];
        SECURE_RANDOM.nextBytes(bytes);
        return bytes;
    }

    /**
     * Generates a random string with the specified length and characters. If all the boolean parameters are false, lowercase will be set to true.
     *
     * @param minLength The min length of the string.
     * @param maxLength The max length of the string.
     * @param lowercase Whether to include lowercase characters.
     * @param uppercase Whether to include uppercase characters.
     * @param number    Whether to include number characters.
     * @param special   Whether to include special characters.
     * @return A random string with a random length between minLength and maxLength and characters.
     */
    public static String randomString(final int minLength, final int maxLength, final boolean lowercase, final boolean uppercase, final boolean number, final boolean special) {
        return randomString(randomInt(minLength, maxLength), lowercase, uppercase, number, special);
    }

    /**
     * Generates a random string with the specified length and characters. If all the boolean parameters are false, lowercase will be set to true.
     *
     * @param length    The length of the string.
     * @param lowercase Whether to include lowercase characters.
     * @param uppercase Whether to include uppercase characters.
     * @param number    Whether to include number characters.
     * @param special   Whether to include special characters.
     * @return A random string with the specified length and characters.
     */
    public static String randomString(final int length, boolean lowercase, boolean uppercase, boolean number, boolean special) {
        if (length < 1) {
            return "";
        }
        if (!lowercase && !uppercase && !number && !special) {
            lowercase = true;
        }
        final StringBuilder builder = new StringBuilder();
        while (builder.length() < length) {
            final char character = (char) ThreadLocalRandom.current().nextInt(256);
            if (lowercase && character >= 'a' && character <= 'z') {
                builder.append(character);
            } else if (uppercase && character >= 'A' && character <= 'Z') {
                builder.append(character);
            } else if (number && character >= '0' && character <= '9') {
                builder.append(character);
            } else if (special) {
                builder.append(character);
            }
        }
        return builder.toString();
    }

    /**
     * @return A random IP address.
     */
    public static String getRandomIp() {
        final byte[] bytes = new byte[4];
        ThreadLocalRandom.current().nextBytes(bytes);
        return IntStream.range(0, 4).mapToObj(value -> String.valueOf(bytes[value] & 0xFF)).collect(Collectors.joining("."));
    }

}