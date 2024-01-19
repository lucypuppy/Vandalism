/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import de.nekosarekawaii.vandalism.util.game.ServerUtil;
import net.minecraft.command.CommandSource;

public class CopyServerIPCommand extends AbstractCommand {

    public CopyServerIPCommand() {
        super(
                "Copies the ip address of the server your are currently connected to into your clipboard.",
                Category.MISC,
                "copyserverip",
                "serveripcopy",
                "copyserveraddress",
                "serveraddresscopy",
                "copyip",
                "copyserverip"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            this.copyAddress(false);
            return SINGLE_SUCCESS;
        });
        builder.then(argument("entire-address", BoolArgumentType.bool())
                .executes(context -> {
                    this.copyAddress(BoolArgumentType.getBool(context, "entire-address"));
                    return SINGLE_SUCCESS;
                })
        );
    }

    private void copyAddress(final boolean entireAddress) {
        if (this.mc.isInSingleplayer()) {
            ChatUtil.errorChatMessage("You are in Single-player.");
            return;
        }
        this.mc.keyboard.setClipboard(ServerUtil.getLastServerInfo().address + (entireAddress ? " | " + this.mc.getNetworkHandler().getConnection().getAddress().toString() : ""));
        ChatUtil.infoChatMessage("Server IP copied into the Clipboard.");
    }

}
