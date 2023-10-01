package de.vandalismdevelopment.vandalism.feature.impl.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SoundArgumentType implements ArgumentType<SoundEvent> {

    private final static DynamicCommandExceptionType notExisting = new DynamicCommandExceptionType(id -> Text.literal("No Sound with the id " + id + " has been found!"));

    private final List<String> ids;

    public SoundArgumentType() {
        this.ids = new ArrayList<>();
        for (final SoundEvent soundEvent : Registries.SOUND_EVENT) {
            final Identifier id = soundEvent.getId();
            if (id.getNamespace().equals("minecraft")) {
                this.ids.add(id.getPath());
            }
        }
    }

    public static SoundArgumentType create() {
        return new SoundArgumentType();
    }

    public static SoundEvent get(final CommandContext<?> context) {
        return context.getArgument("sound", SoundEvent.class);
    }

    @Override
    public SoundEvent parse(final StringReader reader) throws CommandSyntaxException {
        final String argument = reader.readString();
        SoundEvent sound = null;
        for (final SoundEvent soundEvent : Registries.SOUND_EVENT) {
            final Identifier id = soundEvent.getId();
            if (id.getNamespace().equals("minecraft") && id.getPath().equals(argument)) {
                sound = soundEvent;
                break;
            }
        }
        if (sound == null) throw notExisting.create(argument);
        return sound;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(this.ids, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return this.ids.subList(0, 2);
    }

}
