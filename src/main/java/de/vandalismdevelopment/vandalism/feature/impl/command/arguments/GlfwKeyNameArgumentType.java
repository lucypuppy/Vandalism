package de.vandalismdevelopment.vandalism.feature.impl.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.vandalismdevelopment.vandalism.util.GlfwKeyName;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GlfwKeyNameArgumentType implements ArgumentType<GlfwKeyName> {

    private final static DynamicCommandExceptionType NOT_EXISTING = new DynamicCommandExceptionType(
            name -> Text.literal("No glfw key with the name " + name + " has been found!")
    );

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
        return GlfwKeyName.getGlfwKeyNameByName(reader.readString().replace("-", " "));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(this.names, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return this.names.subList(0, 2);
    }

}
