/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Pair;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CopyServerCommand extends Command {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public CopyServerCommand() {
        super(
                "Copies the data of the ip address from the server your are currently connected to.",
                Category.MISC,
                "copyserver",
                "servercopy",
                "copyip"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        for (final Mode mode : Mode.values()) {
            final String name = mode.name().toLowerCase();
            builder.then(literal(name).executes(context -> {
                switch (mode) {
                    case PORT -> this.copyServer(false, false);
                    case FULL_RESOLVED -> this.copyServer(true, true);
                    case PORT_RESOLVED -> this.copyServer(true, false);
                    default -> this.copyServer(false, true);
                }
                return SINGLE_SUCCESS;
            }));
        }
    }

    private void copyServer(final boolean resolved, final boolean full) {
        if (mc.isInSingleplayer()) {
            ChatUtil.errorChatMessage("You are in singleplayer.");
            return;
        }
        if (resolved) {
            final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
            if (networkHandler != null) {
                EXECUTOR.submit(() -> {
                    final Pair<String, Integer> address = ServerUtil.resolveServerAddress(networkHandler.getConnection().getAddress().toString());
                    mc.keyboard.setClipboard((full ? address.getLeft() + ":" : "") + address.getRight());
                });
            } else {
                ChatUtil.errorChatMessage("You are not connected to a server.");
            }
        } else {
            final ServerInfo serverInfo = mc.getCurrentServerEntry();
            if (serverInfo != null) {
                final Pair<String, Integer> address = ServerUtil.splitServerAddress(serverInfo.address);
                mc.keyboard.setClipboard((full ? address.getLeft() + ":" : "") + address.getRight());
            } else {
                ChatUtil.errorChatMessage("You are not connected to a server.");
            }
        }
        ChatUtil.infoChatMessage("Server IP Data copied into the clipboard.");
    }

    private enum Mode {
        FULL,
        PORT,
        FULL_RESOLVED,
        PORT_RESOLVED
    }

}
