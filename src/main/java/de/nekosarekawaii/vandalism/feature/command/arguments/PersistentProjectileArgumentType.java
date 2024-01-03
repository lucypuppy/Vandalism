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
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PersistentProjectileArgumentType implements ArgumentType<PersistentProjectileArgumentType.PersistentProjectile> {

    private static final DynamicCommandExceptionType NOT_EXISTING = new DynamicCommandExceptionType(
            name -> Text.literal("No persistent projectile with the name " + name + " has been found!")
    );

    private final List<String> names = new ArrayList<>();

    public PersistentProjectileArgumentType() {
        this.names.addAll(Arrays.stream(PersistentProjectile.values()).map(persistentProjectile -> persistentProjectile.name().toLowerCase()).toList());
    }

    public static PersistentProjectileArgumentType create() {
        return new PersistentProjectileArgumentType();
    }

    public static PersistentProjectile get(final CommandContext<?> context) {
        return context.getArgument("persistent-projectile", PersistentProjectile.class);
    }

    @Override
    public PersistentProjectile parse(final StringReader reader) throws CommandSyntaxException {
        final String argument = reader.readString().toUpperCase();
        try {
            return PersistentProjectile.valueOf(argument);
        } catch (IllegalArgumentException exception) {
            throw NOT_EXISTING.create(argument);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(this.names, builder);
    }

    public enum PersistentProjectile {

        ARROW,
        TRIDENT,
        SPECTRAL_ARROW

    }

}
