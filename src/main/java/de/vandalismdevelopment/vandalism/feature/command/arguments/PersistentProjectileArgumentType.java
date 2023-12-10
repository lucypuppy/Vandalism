package de.vandalismdevelopment.vandalism.feature.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PersistentProjectileArgumentType implements ArgumentType<PersistentProjectileArgumentType.PersistentProjectile> {

    private static final DynamicCommandExceptionType NOT_EXISTING = new DynamicCommandExceptionType(
            name -> Text.literal("No persistent projectile with the name " + name + " has been found!")
    );

    private final List<String> names = new ArrayList<>();

    public PersistentProjectileArgumentType() {
        this.names.addAll(Arrays.stream(PersistentProjectile.values()).map(persistentProjectile -> persistentProjectile.name().toLowerCase()).toList());
    }

    public static PersistentProjectileArgumentType create() {
        return new PersistentProjectileArgumentType();
    }

    public static PersistentProjectile get(final CommandContext<?> context) {
        return context.getArgument("persistent-projectile", PersistentProjectile.class);
    }

    @Override
    public PersistentProjectile parse(final StringReader reader) throws CommandSyntaxException {
        final String argument = reader.readString().toUpperCase();
        try {
            return PersistentProjectile.valueOf(argument);
        } catch (final IllegalArgumentException exception) {
            throw NOT_EXISTING.create(argument);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(this.names, builder);
    }

    public enum PersistentProjectile {

        ARROW,
        TRIDENT,
        SPECTRAL_ARROW

    }

}
