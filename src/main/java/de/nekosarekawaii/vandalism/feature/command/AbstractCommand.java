package de.nekosarekawaii.vandalism.feature.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.Feature;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.server.command.CommandManager;
import net.raphimc.vialoader.util.VersionRange;

public abstract class AbstractCommand extends Feature {

    public static final int SINGLE_SUCCESS = com.mojang.brigadier.Command.SINGLE_SUCCESS;
    public static final CommandRegistryAccess REGISTRY_ACCESS = CommandManager.createRegistryAccess(BuiltinRegistries.createWrapperLookup());
    public static final CommandSource COMMAND_SOURCE = new ClientCommandSource(null, MinecraftClient.getInstance()); // Mojang is bad, we are good

    private final String[] aliases;

    public AbstractCommand(String description, Category category, String... aliases) {
        super(null, description, category);

        this.aliases = aliases;
    }

    public AbstractCommand(String description, Category category, VersionRange supportedVersions, String... aliases) {
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

    public String[] getAliases() {
        return aliases;
    }

}
