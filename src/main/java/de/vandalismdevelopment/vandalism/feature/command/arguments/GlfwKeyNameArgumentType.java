package de.vandalismdevelopment.vandalism.feature.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.vandalismdevelopment.vandalism.util.GlfwKeyName;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GlfwKeyNameArgumentType implements ArgumentType<GlfwKeyName> {

    private final List<String> names;

    public GlfwKeyNameArgumentType() {
        this.names = new ArrayList<>();
        this.names.addAll(
                Arrays.stream(
                        GlfwKeyName.values()).map(
                        keyName -> keyName.normalName().replace(" ", "-")
                ).toList()
        );
    }

    public static GlfwKeyNameArgumentType create() {
        return new GlfwKeyNameArgumentType();
    }

    public static GlfwKeyName get(final CommandContext<?> context) {
        return context.getArgument("glfwkeyname", GlfwKeyName.class);
    }

    @Override
    public GlfwKeyName parse(final StringReader reader) throws CommandSyntaxException {
        final String keyName = reader.readString().replace("-", " ");
        if (keyName.equalsIgnoreCase("none")) {
            return GlfwKeyName.UNKNOWN;
        }
        return GlfwKeyName.getGlfwKeyNameByName(keyName);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(this.names, builder);
    }

}
