package de.vandalismdevelopment.vandalism.feature.impl.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.Feature;
import de.vandalismdevelopment.vandalism.feature.impl.script.Script;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;


public class ScriptArgumentType implements ArgumentType<Script> {

    private static final DynamicCommandExceptionType NOT_EXISTING = new DynamicCommandExceptionType(name -> {
        return Text.literal("No script with the name " + name + " has been found!");
    });

    public static ScriptArgumentType create() {
        return new ScriptArgumentType();
    }

    public static Script get(final CommandContext<?> context) {
        return context.getArgument("script", Script.class);
    }

    @Override
    public Script parse(final StringReader reader) throws CommandSyntaxException {
        final String argument = reader.readString();
        final Script script = Vandalism.getInstance().getScriptRegistry().getScripts().get(argument);
        if (script == null) throw NOT_EXISTING.create(argument);
        return script;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Vandalism.getInstance().getScriptRegistry().getScripts().stream().map(Feature::getName), builder);
    }

}
