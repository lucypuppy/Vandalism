package me.nekosarekawaii.foxglove.feature.impl.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.Feature;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * The ModuleArgumentType class represents a custom argument type for a command that expects a Module as an argument.
 * It provides parsing and suggestion functionality for module arguments.
 */
public class ModuleArgumentType implements ArgumentType<Module> {

    private static final DynamicCommandExceptionType notExisting = new DynamicCommandExceptionType(name -> Text.literal("No Module with the name " + name + " has been found!"));

    private static final Collection<String> examples = Foxglove.getInstance().getModuleRegistry().getModules()
            .stream()
            .limit(3)
            .map(Feature::getName)
            .collect(Collectors.toList());

    /**
     * Creates a new instance of ModuleArgumentType.
     *
     * @return The created ModuleArgumentType instance.
     */
    public static ModuleArgumentType create() {
        return new ModuleArgumentType();
    }

    /**
     * Retrieves the Module argument from the command context.
     *
     * @param context The command context.
     * @return The Module argument.
     */
    public static Module get(final CommandContext<?> context) {
        return context.getArgument("module", Module.class);
    }

    /**
     * Parses the input StringReader to retrieve a Module argument.
     * Throws a CommandSyntaxException if the module does not exist.
     *
     * @param reader The StringReader to parse.
     * @return The parsed Module argument.
     * @throws CommandSyntaxException If the module does not exist.
     */
    @Override
    public Module parse(final StringReader reader) throws CommandSyntaxException {
        final String argument = reader.readString().replace("-", " ");
        final Module module = Foxglove.getInstance().getModuleRegistry().getModules().get(argument);
        if (module == null) {
            throw notExisting.create(argument);
        }
        return module;
    }

    /**
     * Provides suggestions for module arguments based on the input context and suggestions builder.
     *
     * @param context The command context.
     * @param builder The suggestions builder.
     * @param <S>     The type of the command source.
     * @return A CompletableFuture containing the suggestions.
     */
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Foxglove.getInstance().getModuleRegistry().getModules().stream().map(feature -> feature.getName().replace(" ", "-")), builder);
    }

    /**
     * Retrieves the examples of module arguments.
     *
     * @return A collection of example module arguments.
     */
    @Override
    public Collection<String> getExamples() {
        return examples;
    }

}
