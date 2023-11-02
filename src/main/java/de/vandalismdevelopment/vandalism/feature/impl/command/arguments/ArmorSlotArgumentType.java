package de.vandalismdevelopment.vandalism.feature.impl.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.vandalismdevelopment.vandalism.util.EnumNameNormalizer;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ArmorSlotArgumentType implements ArgumentType<ArmorSlotArgumentType.ArmorSlot> {

    private final static DynamicCommandExceptionType NOT_EXISTING = new DynamicCommandExceptionType(
            name -> Text.literal("No armor slot with the name " + name + " has been found!")
    );

    private final List<String> names;

    public ArmorSlotArgumentType() {
        this.names = new ArrayList<>();
        this.names.addAll(
                Arrays.stream(
                        ArmorSlot.values()).map(
                        armorSlot -> armorSlot.normalName().replace(" ", "-")
                ).toList()
        );
    }

    public static ArmorSlotArgumentType create() {
        return new ArmorSlotArgumentType();
    }

    public static ArmorSlot get(final CommandContext<?> context) {
        return context.getArgument("armor-slot", ArmorSlot.class);
    }

    @Override
    public ArmorSlot parse(final StringReader reader) throws CommandSyntaxException {
        final String argument = reader.readString().replace("-", " ");
        final ArmorSlot armorSlot = ArmorSlot.getByName(argument);
        if (armorSlot == null) throw NOT_EXISTING.create(argument);
        return armorSlot;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(this.names, builder);
    }

    public enum ArmorSlot implements EnumNameNormalizer {

        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS;

        private final String normalName;

        ArmorSlot() {
            this.normalName = this.normalizeName(this.name());
        }

        @Override
        public String normalName() {
            return this.normalName;
        }

        public static ArmorSlot getByName(final String name) {
            for (final ArmorSlot armorSlot : values()) {
                if (armorSlot.normalName().equalsIgnoreCase(name)) {
                    return armorSlot;
                }
            }
            return null;
        }

    }

}
