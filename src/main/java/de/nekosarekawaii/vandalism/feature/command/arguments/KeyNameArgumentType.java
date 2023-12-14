package de.nekosarekawaii.vandalism.feature.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.nekosarekawaii.vandalism.util.render.InputType;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class KeyNameArgumentType implements ArgumentType<Integer> {

    private static final DynamicCommandExceptionType notExisting = new DynamicCommandExceptionType(
            id -> Text.literal("No enchantment with the id " + id + " has been found!")
    );

    public static KeyNameArgumentType create() {
        return new KeyNameArgumentType();
    }

    public static int get(final CommandContext<?> context) {
        return context.getArgument("key-name", int.class);
    }

    @Override
    public Integer parse(final StringReader reader) throws CommandSyntaxException {
        final String keyName = reader.readString().replace("-", " ").toUpperCase(Locale.ROOT);
        if (keyName.equalsIgnoreCase("none") || keyName.equalsIgnoreCase("unknown")) {
            return GLFW.GLFW_KEY_UNKNOWN;
        } else {
            if (!InputType.FIELD_NAMES.containsKey(keyName)) {
                throw notExisting.createWithContext(reader, keyName);
            }
            return InputType.FIELD_NAMES.get(keyName);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(InputType.FIELD_NAMES.keySet(), builder);
    }

}
