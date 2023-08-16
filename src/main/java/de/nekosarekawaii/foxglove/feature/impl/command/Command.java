package de.nekosarekawaii.foxglove.feature.impl.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.nekosarekawaii.foxglove.feature.Feature;
import de.nekosarekawaii.foxglove.feature.FeatureType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

import java.util.Arrays;

public abstract class Command extends Feature {

    protected final static int singleSuccess = com.mojang.brigadier.Command.SINGLE_SUCCESS;
    protected final static CommandRegistryAccess registryAccess = CommandManager.createRegistryAccess(BuiltinRegistries.createWrapperLookup());

    protected final static SimpleCommandExceptionType
            notInCreativeMode = new SimpleCommandExceptionType(Text.literal("You must be in creative mode to use this.")),
            notSpaceInHotBar = new SimpleCommandExceptionType(Text.literal("No space in hot bar."));

    private final String[] aliases;
    private final String aliasesString;

    public Command() {
        final CommandInfo commandInfo = this.getClass().getAnnotation(CommandInfo.class);
        this.setName(commandInfo.name());
        this.setDescription(commandInfo.description());
        this.setType(FeatureType.COMMAND);
        this.setCategory(commandInfo.category());
        this.setExperimental(commandInfo.isExperimental());
        this.aliases = commandInfo.aliases();
        this.aliasesString = Arrays.toString(this.aliases);
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public String getAliasesString() {
        return this.aliasesString;
    }

    protected static <T> RequiredArgumentBuilder<CommandSource, T> argument(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    protected static LiteralArgumentBuilder<CommandSource> literal(final String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public abstract void build(final LiteralArgumentBuilder<CommandSource> builder);

    @Override
    public String toString() {
        return '{' +
                "name=" + this.getName() +
                ", category=" + this.getCategory() +
                ", experimental=" + this.isExperimental() +
                ", aliases=" + this.getAliasesString() +
                '}';
    }

}
