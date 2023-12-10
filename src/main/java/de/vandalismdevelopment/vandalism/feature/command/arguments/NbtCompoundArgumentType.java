package de.vandalismdevelopment.vandalism.feature.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;

import static net.minecraft.nbt.StringNbtReader.EXPECTED_VALUE;

public class NbtCompoundArgumentType implements ArgumentType<NbtCompound> {

    public static NbtCompoundArgumentType create() {
        return new NbtCompoundArgumentType();
    }

    public static NbtCompound get(final CommandContext<?> context) {
        return context.getArgument("nbt", NbtCompound.class);
    }

    @Override
    public NbtCompound parse(final StringReader reader) throws CommandSyntaxException {
        reader.skipWhitespace();
        if (!reader.canRead()) throw EXPECTED_VALUE.createWithContext(reader);
        final StringBuilder b = new StringBuilder();
        int open = 0;
        while (reader.canRead()) {
            if (reader.peek() == '{') {
                open++;
            } else if (reader.peek() == '}') {
                open--;
            }
            if (open == 0)
                break;
            b.append(reader.read());
        }
        reader.expect('}');
        b.append('}');
        return StringNbtReader.parse(b.toString()
                .replace("$", "\u00a7")
                .replace("\u00a7\u00a7", "$")
        );
    }

}