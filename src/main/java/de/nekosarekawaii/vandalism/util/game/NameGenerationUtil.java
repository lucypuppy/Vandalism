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

package de.nekosarekawaii.vandalism.util.game;

import java.util.concurrent.ThreadLocalRandom;

public class NameGenerationUtil {

    private static final String UPPER_CASE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "_";
    private static final String ALL_CHARACTERS = UPPER_CASE_CHARACTERS + LOWER_CASE_CHARACTERS + DIGITS + SPECIAL_CHARACTERS;

    private static final String[] USERNAME_PARTS = {
            "PvP",
            "Im",
            "Craft",
            "Player",
            "Gamer",
            "X",
            "123",
            "HD",
            "Legend",
            "Pro",
            "TV",
            "YT",
            "MC",
            "Gaming",
            "Gamer",
            "Cx",
            "Good",
            "Pro",
            "Elite",
            "Master",
            "Awesome",
            "Cool",
            "Epic",
            "Super",
            "Ultra",
            "Hyper",
            "Mega",
            "Noob",
            "No",
            "Yes",
            "God",
            "IAM",
            "Like",
            "Mode",
            "Speed",
            "Go",
            "Ivan",
            "Alex",
            "Sasha",
            "Dima",
            "Vlad",
            "Nico",
            "Niko",
            "Anna",
            "Anya",
            "Paul",
            "Pavel",
            "Pavlov",
            "Justin",
            "Dustin",
            "Damian",
            "Damon",
            "Dmitri",
            "Dmitriy",
            "Patrick",
            "Patric",
            "Patricio",
            "Lena",
            "Lina",
            "Elena",
            "Elen",
            "Nick",
            "Nicky",
            "Nikita",
            "Lisa",
            "Liza",
            "Lizaveta",
            "Fire",
            "Water",
            "Earth",
            "Air",
            "Liquid",
            "Solid",
            "Gas",
            "Plasma",
            "Magma",
            "Lava",
            "Stone",
            "Rock",
            "Metal",
            "Iron",
            "Gold",
            "Diamond",
            "Emerald",
            "Ruby",
            "Sapphire",
            "Topaz",
            "Amethyst",
            "Quartz",
            "Obsidian",
            "Coal",
            "Redstone",
            "Lapis",
            "Lazuli",
            "Lazurite",
            "Lazur",
            "Laz",
            "Lazzy",
            "Twitch",
            "Cuz"
    };

    public static String generateUsername() {
        String username;
        final String part1 = USERNAME_PARTS[ThreadLocalRandom.current().nextInt(USERNAME_PARTS.length)];
        final String part2 = USERNAME_PARTS[ThreadLocalRandom.current().nextInt(USERNAME_PARTS.length)];
        final int additionalChars = ThreadLocalRandom.current().nextInt(Math.max(0, part1.length() + part2.length()) + 2);
        final StringBuilder additionalCharsBuilder = new StringBuilder();
        final String allowedChars = ALL_CHARACTERS;
        for (int i = 0; i < additionalChars; i++) {
            additionalCharsBuilder.append(allowedChars.charAt(ThreadLocalRandom.current().nextInt(allowedChars.length())));
        }
        username = part1 + part2 + additionalCharsBuilder;
        if (username.length() > 16) username = username.substring(0, 16);
        return username;
    }


}
