package me.nekosarekawaii.foxglove.feature.impl.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.nekosarekawaii.foxglove.feature.Feature;
import me.nekosarekawaii.foxglove.feature.FeatureType;
import net.minecraft.command.CommandSource;

import java.util.Arrays;

/**
 * The Command class is an abstract base class for implementing commands in the Foxglove mod.
 * It provides common functionality and structure for commands.
 */
public abstract class Command extends Feature {

    protected final static int SINGLE_SUCCESS = com.mojang.brigadier.Command.SINGLE_SUCCESS;

    private final String[] aliases;
    private final String aliasesString;

    /**
     * Constructs a new Command object and initializes its properties based on the CommandInfo annotation of the implementing class.
     */
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

    /**
     * Returns an array of aliases for the command.
     *
     * @return The aliases for the command.
     */
    public String[] getAliases() {
        return this.aliases;
    }

    /**
     * Returns a string representation of the aliases for the command.
     *
     * @return The string representation of the aliases for the command.
     */
    public String getAliasesString() {
        return this.aliasesString;
    }

    /**
     * Creates a new RequiredArgumentBuilder for the command with the specified name and argument type.
     *
     * @param name The name of the argument.
     * @param type The argument type.
     * @param <T>  The type of the argument.
     * @return The created RequiredArgumentBuilder.
     */
    protected static <T> RequiredArgumentBuilder<CommandSource, T> argument(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    /**
     * Creates a new LiteralArgumentBuilder for the command with the specified name.
     *
     * @param name The name of the literal.
     * @return The created LiteralArgumentBuilder.
     */
    protected static LiteralArgumentBuilder<CommandSource> literal(final String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    /**
     * Builds the command using a LiteralArgumentBuilder.
     *
     * @param builder The LiteralArgumentBuilder to build the command.
     */
    public abstract void build(final LiteralArgumentBuilder<CommandSource> builder);

    /**
     * Returns a string representation of the Command object.
     *
     * @return A string representation of the Command object.
     */
    @Override
    public String toString() {
        return "{" +
                "name=" + this.getName() +
                ", category=" + this.getCategory() +
                ", experimental=" + this.isExperimental() +
                ", aliases=" + this.getAliasesString() +
                '}';
    }

}
