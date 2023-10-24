package de.vandalismdevelopment.vandalism.feature.impl.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.script.Script;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


public class ScriptArgumentType implements ArgumentType<Script> {

    private final static DynamicCommandExceptionType NOT_EXISTING = new DynamicCommandExceptionType(name -> {
        return Text.literal("No Script with the name " + name + " has been found!");
    });

    private final static Collection<String> EXAMPLES = Vandalism.getInstance().getScriptRegistry().getScripts()
            .stream()
            .limit(3)
            .map(Script::getName)
            .collect(Collectors.toList());

    public static ScriptArgumentType create() {
        return new ScriptArgumentType();
    }

    public static Script get(final CommandContext<?> context) {
        return context.getArgument("script", Script.class);
    }

    @Override
    public Script parse(final StringReader reader) throws CommandSyntaxException {
        final String argument = reader.readString().replace("-", " ");
        final Script script = Vandalism.getInstance().getScriptRegistry().getScripts().get(argument);
        if (script == null) throw NOT_EXISTING.create(argument);
        return script;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Vandalism.getInstance().getScriptRegistry().getScripts().stream().map(script -> {
            return script.getName().replace(" ", "-");
        }), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

}
