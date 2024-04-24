/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EnchantmentArgumentType implements ArgumentType<Enchantment> {

    private static final DynamicCommandExceptionType NOT_EXISTING = new DynamicCommandExceptionType(
            id -> Text.literal("No enchantment with the id " + id + " has been found!")
    );

    private final List<String> ids;

    public EnchantmentArgumentType() {
        this.ids = new ArrayList<>();
        for (final Enchantment enchantment : Registries.ENCHANTMENT) {
            final Identifier id = Registries.ENCHANTMENT.getId(enchantment);
            if (id != null && id.getNamespace().equals("minecraft")) {
                this.ids.add(id.getPath());
            }
        }
    }

    public static EnchantmentArgumentType create() {
        return new EnchantmentArgumentType();
    }

    public static Enchantment get(final CommandContext<?> context) {
        return context.getArgument("enchantment", Enchantment.class);
    }

    @Override
    public Enchantment parse(final StringReader reader) throws CommandSyntaxException {
        final String argument = reader.readString();
        Enchantment foundEnchantment = null;
        for (final Enchantment enchantment : Registries.ENCHANTMENT) {
            final Identifier id = Registries.ENCHANTMENT.getId(enchantment);
            if (id != null && id.getNamespace().equals("minecraft") && id.getPath().equals(argument)) {
                foundEnchantment = enchantment;
                break;
            }
        }
        if (foundEnchantment == null) throw NOT_EXISTING.create(argument);
        return foundEnchantment;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(this.ids, builder);
    }

}
