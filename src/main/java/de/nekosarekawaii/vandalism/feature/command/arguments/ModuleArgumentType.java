/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;


public class ModuleArgumentType implements ArgumentType<AbstractModule> {

    private static final DynamicCommandExceptionType NOT_EXISTING = new DynamicCommandExceptionType(
            name -> Text.literal("No module with the name " + name + " has been found!")
    );

    public static ModuleArgumentType create() {
        return new ModuleArgumentType();
    }

    public static AbstractModule get(final CommandContext<?> context) {
        return context.getArgument("module", AbstractModule.class);
    }

    @Override
    public AbstractModule parse(final StringReader reader) throws CommandSyntaxException {
        final String argument = reader.readString().replace("-", " ").replace("_", "-");
        final AbstractModule module = Vandalism.getInstance().getModuleManager().getByName(argument, true);
        if (module == null) {
            throw NOT_EXISTING.create(argument);
        }
        return module;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(
                Vandalism.getInstance().getModuleManager().getList().stream().map(
                        feature -> feature.getName().replace("-", "_").replace(" ", "-")
                ),
                builder
        );
    }

}
