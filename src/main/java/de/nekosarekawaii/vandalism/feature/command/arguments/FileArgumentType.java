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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FileArgumentType implements ArgumentType<File> {

    private static final DynamicCommandExceptionType NOT_EXISTING = new DynamicCommandExceptionType(
            file -> Text.literal("The file " + file + " has not been found!")
    );

    private final File path;

    public FileArgumentType(final File path) {
        this.path = path;
    }

    public static FileArgumentType create(final File path) {
        return new FileArgumentType(path);
    }

    public static File get(final CommandContext<?> context) {
        return context.getArgument("file", File.class);
    }

    @Override
    public File parse(final StringReader reader) throws CommandSyntaxException {
        final String filePath = reader.readQuotedString();
        final File file = new File(filePath);
        if (file.exists()) return file;
        throw NOT_EXISTING.createWithContext(reader, filePath);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        final List<String> files = new ArrayList<>();
        if (this.path != null && this.path.isDirectory()) {
            final File[] filesInDirectory = this.path.listFiles();
            if (filesInDirectory != null) {
                for (final File file : filesInDirectory) {
                    if (file.isFile()) {
                        files.add(file.getName());
                    }
                }
            }
        }
        return CommandSource.suggestMatching(files, builder);
    }

}
