package de.nekosarekawaii.foxglove.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.command.Command;
import net.minecraft.command.CommandSource;

public class ChatClearCommand extends Command {

    public ChatClearCommand() {
        super(
                "Chat Clear",
                "Clears your clientside chat.",
                FeatureCategory.MISC,
                false,
                "chatclear",
                "clearchat",
                "cc"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            mc().inGameHud.getChatHud().clear(false);
            return singleSuccess;
        });
    }

}
