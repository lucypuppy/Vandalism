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

package de.nekosarekawaii.vandalism.feature.command.impl.misc.copy;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.Command;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.ServerUtil;
import net.minecraft.command.CommandSource;

public class CopyServerVersionCommand extends Command {

    public CopyServerVersionCommand() {
        super(
                "Lets you copy the brand of the server you are currently connected to.",
                Category.MISC,
                "copyserverversion",
                "copyversion",
                "copyver"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            if (this.mc.isInSingleplayer()) {
                ChatUtil.errorChatMessage("You are in singleplayer.");
            } else if (ServerUtil.lastServerExists()) {
                this.mc.keyboard.setClipboard(ServerUtil.getLastServerInfo().version.getString());
                ChatUtil.infoChatMessage("Server version copied into the clipboard.");
            }
            return SINGLE_SUCCESS;
        });
    }

}
