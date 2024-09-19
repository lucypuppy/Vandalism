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

import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.function.Function;
import java.util.function.Supplier;

public enum Placeholders {

    USERNAME(() -> MinecraftClient.getInstance().getSession().getUsername(), description -> {
        description.append("Your current Username.");
        return description;
    }),
    UUID(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return player.getUuidAsString();
    }, description -> {
        description.append("Your current UUID.");
        return description;
    }),
    RANDOM(() -> String.valueOf(RandomUtils.randomInt(1000, 9999)), description -> {
        description.append("A random number between 1000 and 9999.");
        return description;
    }),
    X(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getX());
    }, description -> {
        description.append("Your current X position.");
        return description;
    }),
    Y(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getY());
    }, description -> {
        description.append("Your current Y position.");
        return description;
    }),
    Z(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getZ());
    }, description -> {
        description.append("Your current Z position.");
        return description;
    }),
    YAW(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getYaw());
    }, description -> {
        description.append("Your current Yaw.");
        return description;
    }),
    PITCH(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getPitch());
    }, description -> {
        description.append("Your current Pitch.");
        return description;
    }),
    DAY(() -> String.valueOf(LocalDateTime.now().getDayOfMonth()), description -> {
        description.append("The current day of the month.");
        return description;
    }),
    MONTH(() -> String.valueOf(LocalDateTime.now().getMonth()), description -> {
        description.append("The current month.");
        return description;
    }),
    YEAR(() -> String.valueOf(LocalDateTime.now().getYear()), description -> {
        description.append("The current year.");
        return description;
    }),
    HOURS(() -> String.valueOf(LocalDateTime.now().getHour()), description -> {
        description.append("The current hour.");
        return description;
    }),
    MINUTES(() -> String.valueOf(LocalDateTime.now().getMinute()), description -> {
        description.append("The current minute.");
        return description;
    }),
    SECONDS(() -> String.valueOf(LocalDateTime.now().getSecond()), description -> {
        description.append("The current second.");
        return description;
    }),
    MILLISECONDS(() -> String.valueOf(LocalDateTime.now().getLong(ChronoField.MILLI_OF_SECOND)), description -> {
        description.append("The current millisecond.");
        return description;
    }),
    MOD_NAME(() -> FabricBootstrap.MOD_NAME, description -> {
        description.append("The name of this client.");
        return description;
    }),
    MOD_VERSION(() -> FabricBootstrap.MOD_VERSION, description -> {
        description.append("The version of this client.");
        return description;
    }),
    MOD_AUTHORS(() -> FabricBootstrap.MOD_AUTHORS, description -> {
        description.append("The authors of this client.");
        return description;
    });

    public static final String PLACEHOLDER_CHAR = "%";

    private final Supplier<String> replacement;
    private final Function<StringBuilder, StringBuilder> description;

    Placeholders(final Supplier<String> replacement, final Function<StringBuilder, StringBuilder> description) {
        this.replacement = replacement;
        this.description = description;
    }

    public static String applyReplacements(String text) {
        if (!text.contains(PLACEHOLDER_CHAR)) return text;
        for (final Placeholders variable : values()) {
            final String sequence = PLACEHOLDER_CHAR + variable + PLACEHOLDER_CHAR;
            if (StringUtils.contains(text, sequence)) {
                text = StringUtils.replaceAll(text, sequence, variable.replacement.get());
            }
        }
        return text;
    }

    public String getDescription() {
        return this.description.apply(new StringBuilder()).toString();
    }

}