package de.nekosarekawaii.foxglove.feature.impl.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.feature.Feature;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


public class ModuleArgumentType implements ArgumentType<Module> {

    private final static DynamicCommandExceptionType notExisting = new DynamicCommandExceptionType(name -> Text.literal("No Module with the name " + name + " has been found!"));

    private final static Collection<String> examples = Foxglove.getInstance().getModuleRegistry().getModules()
            .stream()
            .limit(3)
            .map(Feature::getName)
            .collect(Collectors.toList());

    public static ModuleArgumentType create() {
        return new ModuleArgumentType();
    }

    public static Module get(final CommandContext<?> context) {
        return context.getArgument("module", Module.class);
    }

    @Override
    public Module parse(final StringReader reader) throws CommandSyntaxException {
        final String argument = reader.readString().replace("-", " ");
        final Module module = Foxglove.getInstance().getModuleRegistry().getModules().get(argument);
        if (module == null) {
            throw notExisting.create(argument);
        }
        return module;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Foxglove.getInstance().getModuleRegistry().getModules().stream().map(feature -> feature.getName().replace(" ", "-")), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return examples;
    }

}
