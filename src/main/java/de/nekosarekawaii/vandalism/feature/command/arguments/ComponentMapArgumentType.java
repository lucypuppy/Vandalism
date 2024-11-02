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
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.nekosarekawaii.vandalism.util.ComponentMapReader;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.component.ComponentMap;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ComponentMapArgumentType implements ArgumentType<ComponentMap> {

    private static final Collection<String> EXAMPLES = List.of("{foo=bar}");
    private final ComponentMapReader reader;

    public ComponentMapArgumentType(CommandRegistryAccess commandRegistryAccess) {
        this.reader = new ComponentMapReader(commandRegistryAccess);
    }

    public static ComponentMapArgumentType componentMap(CommandRegistryAccess commandRegistryAccess) {
        return new ComponentMapArgumentType(commandRegistryAccess);
    }

    public static <S extends CommandSource> ComponentMap getComponentMap(CommandContext<S> context, String name) {
        return context.getArgument(name, ComponentMap.class);
    }

    @Override
    public ComponentMap parse(StringReader reader) throws CommandSyntaxException {
        return this.reader.consume(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return this.reader.getSuggestions(builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

}