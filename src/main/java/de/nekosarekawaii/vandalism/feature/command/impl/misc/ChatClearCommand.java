/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.feature.command.impl.misc;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.command.Command;
import net.minecraft.command.CommandSource;

import java.io.File;
import java.io.IOException;

public class ChatClearCommand extends Command {

    public ChatClearCommand() {
        super("Clears your clientside chat (also allows you to clear your sent history).", Category.MISC, "chatclear", "clearchat", "cc");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            this.mc.inGameHud.getChatHud().clear(false);
            return SINGLE_SUCCESS;
        });
        builder.then(argument("clear-sent-history", BoolArgumentType.bool()).executes(context -> {
            final boolean clearSentHistory = BoolArgumentType.getBool(context, "clear-sent-history");
            if (clearSentHistory) {
                this.mc.getCommandHistoryManager().getHistory().clear();
                final File commandHistoryFile = new File(this.mc.runDirectory, "command_history.txt");
                if (commandHistoryFile.exists()) {
                    if (!commandHistoryFile.delete()) {
                        Vandalism.getInstance().getLogger().error("Failed to delete command history file.");
                    } else {
                        try {
                            if (!commandHistoryFile.createNewFile()) {
                                Vandalism.getInstance().getLogger().error("Failed to create new command history file.");
                            }
                        } catch (IOException e) {
                            Vandalism.getInstance().getLogger().error("Failed to create new command history file.", e);
                        }
                    }
                }
            }
            this.mc.inGameHud.getChatHud().clear(clearSentHistory);
            return SINGLE_SUCCESS;
        }));
    }

}
