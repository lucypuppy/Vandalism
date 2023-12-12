package de.vandalismdevelopment.vandalism.feature.command.impl.misc;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.command.AbstractCommand;
import net.minecraft.command.CommandSource;

import java.io.File;
import java.io.IOException;

public class ChatClearCommand extends AbstractCommand {

    public ChatClearCommand() {
        super(
                "Clears your clientside chat (also allows you to clear your sent history).",
                Category.MISC,
                "chatclear",
                "clearchat",
                "cc"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            this.mc.inGameHud.getChatHud().clear(false);
            return SINGLE_SUCCESS;
        });

        builder.then(argument("clear-sent-history", BoolArgumentType.bool())
                .executes(context -> {
                    final boolean clearSentHistory = BoolArgumentType.getBool(context, "clear-sent-history");
                    if (clearSentHistory) {
                        final File commandHistoryFile = new File(this.mc.runDirectory, "command_history.txt");
                        if (commandHistoryFile.exists()) {
                            if (!commandHistoryFile.delete()) {
                                Vandalism.getInstance().getLogger().error("Failed to delete command history file.");
                            } else {
                                try {
                                    if (!commandHistoryFile.createNewFile()) {
                                        Vandalism.getInstance().getLogger().error("Failed to create new command history file.");
                                    }
                                } catch (final IOException e) {
                                    Vandalism.getInstance().getLogger().error("Failed to create new command history file.", e);
                                }
                            }
                        }
                    }
                    this.mc.inGameHud.getChatHud().clear(clearSentHistory);
                    return SINGLE_SUCCESS;
                })
        );
    }

}
