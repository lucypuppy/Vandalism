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

package de.nekosarekawaii.vandalism.feature.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.Feature;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.raphimc.vialoader.util.VersionRange;

@Getter
public abstract class Command extends Feature {

    public static final int SINGLE_SUCCESS = com.mojang.brigadier.Command.SINGLE_SUCCESS;
    public static final CommandRegistryAccess REGISTRY_ACCESS = CommandManager.createRegistryAccess(BuiltinRegistries.createWrapperLookup());
    public static final CommandSource COMMAND_SOURCE = new ClientCommandSource(null, MinecraftClient.getInstance()); // Mojang is bad, we are good

    private final String[] aliases;

    public Command(String description, Category category, String... aliases) {
        super(null, description, category);

        this.aliases = aliases;
    }

    public Command(String description, Category category, VersionRange supportedVersions, String... aliases) {
        super(null, description, category, supportedVersions);

        this.aliases = aliases;
    }

    public abstract void build(final LiteralArgumentBuilder<CommandSource> builder);

    public void publish(final CommandDispatcher<CommandSource> dispatcher) {
        for (final String alias : getAliases()) {
            final LiteralArgumentBuilder<CommandSource> builder = LiteralArgumentBuilder.literal(alias);
            build(builder);
            dispatcher.register(builder);
        }
    }

    protected static <T> RequiredArgumentBuilder<CommandSource, T> argument(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    protected static LiteralArgumentBuilder<CommandSource> literal(final String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public ServerCommandSource createServerCommandSource() {
        return new ServerCommandSource(null, mc.player.getPos(), null, null, 0, null, null, null, null);
    }

    @Override
    public String getName() {
        return aliases[0];
    }

}
