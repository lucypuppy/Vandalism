package de.vandalismdevelopment.vandalism.feature.impl.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;


public class ModuleArgumentType implements ArgumentType<Module> {

    private static final DynamicCommandExceptionType NOT_EXISTING = new DynamicCommandExceptionType(
            name -> Text.literal("No module with the name " + name + " has been found!")
    );

    public static ModuleArgumentType create() {
        return new ModuleArgumentType();
    }

    public static Module get(final CommandContext<?> context) {
        return context.getArgument("module", Module.class);
    }

    @Override
    public Module parse(final StringReader reader) throws CommandSyntaxException {
        final String argument = reader.readString().replace("-", " ");
        final Module module = Vandalism.getInstance().getModuleRegistry().getModules().get(argument);
        if (module == null) {
            throw NOT_EXISTING.create(argument);
        }
        return module;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(
                Vandalism.getInstance().getModuleRegistry().getModules().stream().map(
                        feature -> feature.getName().replace(" ", "-")
                ),
                builder
        );
    }

}
