package de.vandalismdevelopment.vandalism.feature.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;


public class ModuleArgumentType implements ArgumentType<AbstractModule> {

    private static final DynamicCommandExceptionType NOT_EXISTING = new DynamicCommandExceptionType(
            name -> Text.literal("No module with the name " + name + " has been found!")
    );

    public static ModuleArgumentType create() {
        return new ModuleArgumentType();
    }

    public static AbstractModule get(final CommandContext<?> context) {
        return context.getArgument("module", AbstractModule.class);
    }

    @Override
    public AbstractModule parse(final StringReader reader) throws CommandSyntaxException {
        final String argument = reader.readString().replace("-", " ");
        final AbstractModule module = Vandalism.getInstance().getModuleRegistry().getList().get(argument);
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
