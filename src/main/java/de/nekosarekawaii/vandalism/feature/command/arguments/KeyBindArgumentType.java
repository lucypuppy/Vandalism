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

package de.nekosarekawaii.vandalism.feature.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.nekosarekawaii.vandalism.util.render.util.InputType;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public class KeyBindArgumentType implements ArgumentType<Integer> {

    private static final DynamicCommandExceptionType NOT_EXISTING = new DynamicCommandExceptionType(
            name -> Text.literal("No key with the name " + name + " has been found!")
    );

    public static KeyBindArgumentType create() {
        return new KeyBindArgumentType();
    }

    public static int get(final CommandContext<?> context) {
        return context.getArgument("key-bind", int.class);
    }

    @Override
    public Integer parse(final StringReader reader) throws CommandSyntaxException {
        final String input = reader.canRead(2) ? reader.readString() : String.valueOf(reader.read());
        final String fixedInput = (input.equals("-") ? input : input.replace("-", " ")).toUpperCase();
        if (!InputType.FIELD_NAMES.containsKey(fixedInput)) {
            throw NOT_EXISTING.createWithContext(reader, input);
        }
        return InputType.FIELD_NAMES.get(fixedInput);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(InputType.FIELD_NAMES.keySet().stream().map(keyName -> keyName.replace(" ", "-")), builder);
    }

}
