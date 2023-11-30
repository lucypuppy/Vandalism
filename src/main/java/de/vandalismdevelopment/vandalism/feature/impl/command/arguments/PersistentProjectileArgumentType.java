package de.vandalismdevelopment.vandalism.feature.impl.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.vandalismdevelopment.vandalism.util.interfaces.EnumNameNormalizer;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PersistentProjectileArgumentType implements ArgumentType<PersistentProjectileArgumentType.PersistentProjectile> {

    private final static DynamicCommandExceptionType NOT_EXISTING = new DynamicCommandExceptionType(
            name -> Text.literal("No persistent projectile with the name " + name + " has been found!")
    );

    private final List<String> names;

    public PersistentProjectileArgumentType() {
        this.names = new ArrayList<>();
        this.names.addAll(
                Arrays.stream(
                        PersistentProjectile.values()).map(
                        armorSlot -> armorSlot.normalName().replace(" ", "-")
                ).toList()
        );
    }

    public static PersistentProjectileArgumentType create() {
        return new PersistentProjectileArgumentType();
    }

    public static PersistentProjectile get(final CommandContext<?> context) {
        return context.getArgument("persistent-projectile", PersistentProjectile.class);
    }

    @Override
    public PersistentProjectile parse(final StringReader reader) throws CommandSyntaxException {
        final String argument = reader.readString().replace("-", " ");
        final PersistentProjectile armorSlot = PersistentProjectile.getByName(argument);
        if (armorSlot == null) throw NOT_EXISTING.create(argument);
        return armorSlot;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(this.names, builder);
    }

    public enum PersistentProjectile implements EnumNameNormalizer {

        ARROW,
        TRIDENT,
        SPECTRAL_ARROW;

        private final String normalName;

        PersistentProjectile() {
            this.normalName = this.normalizeName(this.name());
        }

        @Override
        public String normalName() {
            return this.normalName;
        }

        public static PersistentProjectile getByName(final String name) {
            for (final PersistentProjectile armorSlot : values()) {
                if (armorSlot.normalName().equalsIgnoreCase(name)) {
                    return armorSlot;
                }
            }
            return null;
        }

    }

}
