package de.foxglovedevelopment.foxglove.feature.impl.command.arguments;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class GameModeArgumentType implements ArgumentType<GameMode> {

    private final static DynamicCommandExceptionType notExisting = new DynamicCommandExceptionType(name -> Text.literal("No Game Mode with the name or id " + name + " has been found!"));

    private final List<String> names;

    public GameModeArgumentType() {
        this.names = new ArrayList<>();
        this.names.addAll(Arrays.stream(GameMode.values()).map(GameMode::getName).toList());
        this.names.addAll(Arrays.stream(GameMode.values()).map(gameMode -> Integer.toString(gameMode.getId())).toList());
    }

    public static GameModeArgumentType create() {
        return new GameModeArgumentType();
    }

    public static GameMode get(final CommandContext<?> context) {
        return context.getArgument("gamemode", GameMode.class);
    }

    @Override
    public GameMode parse(final StringReader reader) throws CommandSyntaxException {
        final String argument = reader.readString();
        GameMode gameMode = GameMode.byName(argument, null);
        if (gameMode == null) {
            try {
                gameMode = GameMode.byId(Integer.parseInt(argument));
            } catch (final NumberFormatException ignored) {
            }
        }
        if (gameMode == null) throw notExisting.create(argument);
        return gameMode;
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
