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

    USERNAME(() -> MinecraftClient.getInstance().getSession().getUsername(), descriptionBuilder -> {
        descriptionBuilder.append("Your current Username.");
        return descriptionBuilder;
    }),
    UUID(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return player.getUuidAsString();
    }, descriptionBuilder -> {
        descriptionBuilder.append("Your current UUID.");
        return descriptionBuilder;
    }),
    RANDOM(() -> String.valueOf(RandomUtils.randomInt(1000, 9999)), descriptionBuilder -> {
        descriptionBuilder.append("A random number between 1000 and 9999.");
        return descriptionBuilder;
    }),
    X(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getX());
    }, descriptionBuilder -> {
        descriptionBuilder.append("Your current X position.");
        return descriptionBuilder;
    }),
    Y(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getY());
    }, descriptionBuilder -> {
        descriptionBuilder.append("Your current Y position.");
        return descriptionBuilder;
    }),
    Z(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getZ());
    }, descriptionBuilder -> {
        descriptionBuilder.append("Your current Z position.");
        return descriptionBuilder;
    }),
    YAW(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getYaw());
    }, descriptionBuilder -> {
        descriptionBuilder.append("Your current Yaw.");
        return descriptionBuilder;
    }),
    PITCH(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getPitch());
    }, descriptionBuilder -> {
        descriptionBuilder.append("Your current Pitch.");
        return descriptionBuilder;
    }),
    DAY(() -> String.valueOf(LocalDateTime.now().getDayOfMonth()), descriptionBuilder -> {
        descriptionBuilder.append("The current day of the month.");
        return descriptionBuilder;
    }),
    MONTH(() -> String.valueOf(LocalDateTime.now().getMonth()), descriptionBuilder -> {
        descriptionBuilder.append("The current month.");
        return descriptionBuilder;
    }),
    YEAR(() -> String.valueOf(LocalDateTime.now().getYear()), descriptionBuilder -> {
        descriptionBuilder.append("The current year.");
        return descriptionBuilder;
    }),
    HOURS(() -> String.valueOf(LocalDateTime.now().getHour()), descriptionBuilder -> {
        descriptionBuilder.append("The current hour.");
        return descriptionBuilder;
    }),
    MINUTES(() -> String.valueOf(LocalDateTime.now().getMinute()), descriptionBuilder -> {
        descriptionBuilder.append("The current minute.");
        return descriptionBuilder;
    }),
    SECONDS(() -> String.valueOf(LocalDateTime.now().getSecond()), descriptionBuilder -> {
        descriptionBuilder.append("The current second.");
        return descriptionBuilder;
    }),
    MILLISECONDS(() -> String.valueOf(LocalDateTime.now().getLong(ChronoField.MILLI_OF_SECOND)), descriptionBuilder -> {
        descriptionBuilder.append("The current millisecond.");
        return descriptionBuilder;
    }),
    MOD_NAME(() -> FabricBootstrap.MOD_NAME, descriptionBuilder -> {
        descriptionBuilder.append("The name of this client.");
        return descriptionBuilder;
    }),
    MOD_VERSION(() -> FabricBootstrap.MOD_VERSION, descriptionBuilder -> {
        descriptionBuilder.append("The version of this client.");
        return descriptionBuilder;
    }),
    MOD_AUTHORS(() -> FabricBootstrap.MOD_AUTHORS, descriptionBuilder -> {
        descriptionBuilder.append("The authors of this client.");
        return descriptionBuilder;
    });

    public static final String VARIABLE_CHAR = "%";

    private final Supplier<String> scriptReplacement;
    private final Function<StringBuilder, StringBuilder> scriptVariableDescription;

    Placeholders(final Supplier<String> scriptReplacement, final Function<StringBuilder, StringBuilder> scriptVariableDescription) {
        this.scriptReplacement = scriptReplacement;
        this.scriptVariableDescription = scriptVariableDescription;
    }

    public static String applyReplacements(String text) {
        if (!text.contains(VARIABLE_CHAR)) return text;
        for (final Placeholders variable : values()) {
            final String sequence = VARIABLE_CHAR + variable + VARIABLE_CHAR;
            if (StringUtils.contains(text, sequence)) {
                text = StringUtils.replaceAll(text, sequence, variable.scriptReplacement.get());
            }
        }
        return text;
    }

    public String getDescription() {
        return this.scriptVariableDescription.apply(new StringBuilder()).toString();
    }

}