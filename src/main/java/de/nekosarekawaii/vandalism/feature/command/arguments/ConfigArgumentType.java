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

package de.nekosarekawaii.vandalism.feature.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.nekosarekawaii.vandalism.feature.command.impl.misc.ConfigCommand;
import de.nekosarekawaii.vandalism.util.StringUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConfigArgumentType implements ArgumentType<File> {

    private static final DynamicCommandExceptionType NOT_EXISTING = new DynamicCommandExceptionType(
            config -> Text.literal("The config " + config + " has not been found!")
    );

    private static final DynamicCommandExceptionType EMPTY = new DynamicCommandExceptionType(
            config -> Text.literal("You have to specify a config name!")
    );

    private static final DynamicCommandExceptionType INVALID = new DynamicCommandExceptionType(
            config -> Text.literal("The config " + config + " is invalid!")
    );

    protected final File path;

    public ConfigArgumentType(final File path) {
        this.path = path;
    }

    public static ConfigArgumentType create(final File path) {
        return new ConfigArgumentType(path);
    }

    public static File get(final CommandContext<?> context) {
        return context.getArgument("config-name", File.class);
    }

    @Override
    public File parse(final StringReader reader) throws CommandSyntaxException {
        final String configName = reader.readString();
        if (configName.isBlank()) {
            throw EMPTY.createWithContext(reader, configName);
        }
        if (ConfigCommand.INVALID_CONFIG_NAME_PATTERN.matcher(configName).find()) {
            throw INVALID.createWithContext(reader, configName);
        }
        if (this.path != null && this.path.isDirectory()) {
            final File[] filesInDirectory = this.path.listFiles();
            if (filesInDirectory != null) {
                for (final File file : filesInDirectory) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        final String name = StringUtils.replaceLast(file.getName(), ".json", "");
                        if (name.equals(configName)) {
                            return file;
                        }
                    }
                }
            }
        }
        throw NOT_EXISTING.createWithContext(reader, configName);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        final List<String> files = new ArrayList<>();
        if (this.path != null && this.path.isDirectory()) {
            final File[] filesInDirectory = this.path.listFiles();
            if (filesInDirectory != null) {
                for (final File file : filesInDirectory) {
                    final String name = file.getName();
                    if (file.isFile() && name.endsWith(".json")) {
                        files.add(StringUtils.replaceLast(name, ".json", ""));
                    }
                }
            }
        }
        return CommandSource.suggestMatching(files, builder);
    }

}
