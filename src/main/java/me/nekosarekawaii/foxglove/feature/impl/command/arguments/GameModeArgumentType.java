package me.nekosarekawaii.foxglove.feature.impl.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


public class GameModeArgumentType implements ArgumentType<GameMode> {

    private static final DynamicCommandExceptionType notExisting = new DynamicCommandExceptionType(name -> Text.literal("No Game Mode with the name " + name + " has been found!"));

    private static final Collection<String> examples = Arrays.stream(GameMode.values()).limit(3).map(GameMode::getName).collect(Collectors.toList());

    public static GameModeArgumentType create() {
        return new GameModeArgumentType();
    }

    public static GameMode get(final CommandContext<?> context) {
        return context.getArgument("gamemode", GameMode.class);
    }

    @Override
    public GameMode parse(final StringReader reader) throws CommandSyntaxException {
        final String argument = reader.readString();
        final GameMode gameMode = GameMode.byName(argument, null);
        if (gameMode == null) throw notExisting.create(argument);
        return gameMode;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Arrays.stream(GameMode.values()).map(GameMode::getName), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return examples;
    }

}
