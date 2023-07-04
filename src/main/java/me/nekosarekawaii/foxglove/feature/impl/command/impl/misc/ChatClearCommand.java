package me.nekosarekawaii.foxglove.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.command.Command;
import me.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import net.minecraft.command.CommandSource;

@CommandInfo(name = "Chat Clear", aliases = {"chatclear", "clearchat", "cc"}, description = "This command clears the chat.", category = FeatureCategory.MISC)
public class ChatClearCommand extends Command {

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            mc().inGameHud.getChatHud().clear(false);
            return SINGLE_SUCCESS;
        });
    }

}
