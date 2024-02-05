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

package de.nekosarekawaii.vandalism.feature.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.nekosarekawaii.vandalism.util.render.InputType;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class KeyNameArgumentType implements ArgumentType<Integer> {

    private static final DynamicCommandExceptionType NOT_EXISTING = new DynamicCommandExceptionType(
            name -> Text.literal("No key with the name " + name + " has been found!")
    );

    public static KeyNameArgumentType create() {
        return new KeyNameArgumentType();
    }

    public static int get(final CommandContext<?> context) {
        return context.getArgument("key-name", int.class);
    }

    @Override
    public Integer parse(final StringReader reader) throws CommandSyntaxException {
        final String keyName = reader.readString().replace("-", " ").toUpperCase(Locale.ROOT);
        if (keyName.equalsIgnoreCase("none") || keyName.equalsIgnoreCase("unknown")) {
            return GLFW.GLFW_KEY_UNKNOWN;
        } else {
            if (!InputType.FIELD_NAMES.containsKey(keyName)) {
                throw NOT_EXISTING.createWithContext(reader, keyName);
            }
            return InputType.FIELD_NAMES.get(keyName);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(InputType.FIELD_NAMES.keySet(), builder);
    }

}
