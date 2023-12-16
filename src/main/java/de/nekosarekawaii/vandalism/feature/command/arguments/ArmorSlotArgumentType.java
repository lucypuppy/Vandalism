package de.nekosarekawaii.vandalism.feature.command.arguments;

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

public class ArmorSlotArgumentType implements ArgumentType<ArmorSlotArgumentType.ArmorSlot> {

    private static final DynamicCommandExceptionType NOT_EXISTING = new DynamicCommandExceptionType(
            name -> Text.literal("No armor slot with the name " + name + " has been found!")
    );

    private final List<String> names = new ArrayList<>();

    public ArmorSlotArgumentType() {
        this.names.addAll(Arrays.stream(ArmorSlot.values()).map(armorSlot -> armorSlot.name().toLowerCase()).toList());
    }

    public static ArmorSlotArgumentType create() {
        return new ArmorSlotArgumentType();
    }

    public static ArmorSlot get(final CommandContext<?> context) {
        return context.getArgument("armor-slot", ArmorSlot.class);
    }

    @Override
    public ArmorSlot parse(final StringReader reader) throws CommandSyntaxException {
        final String input = reader.readString().toUpperCase();
        try {
            return ArmorSlot.valueOf(input);
        } catch (IllegalArgumentException exception) {
            throw NOT_EXISTING.create(input);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(this.names, builder);
    }

    public enum ArmorSlot {

        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS

    }

}
