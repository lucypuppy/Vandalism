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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import com.mojang.authlib.GameProfile;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class JoinLeaveNotifierModule extends Module implements IncomingPacketListener {

    private final BooleanValue displayGameModeOnJoin = new BooleanValue(
            this,
            "Display Game Mode On Join",
            "Displays the game mode of players that join the server.",
            true
    );

    public JoinLeaveNotifierModule() {
        super(
                "Join Leave Notifier",
                "Notifies you whenever a player connects or disconnects to the server.",
                Category.MISC
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(IncomingPacketEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(IncomingPacketEvent.ID, this);
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        final Packet<?> packet = event.packet;
        if (packet instanceof final PlayerListS2CPacket playerListS2CPacket) {
            if (playerListS2CPacket.getActions().contains(PlayerListS2CPacket.Action.ADD_PLAYER)) {
                for (final PlayerListS2CPacket.Entry entry : playerListS2CPacket.getEntries()) {
                    final GameProfile profile = entry.profile();
                    if (profile != null) {
                        final StringBuilder message = new StringBuilder();
                        message.append(Formatting.DARK_GRAY);
                        message.append("[");
                        message.append(Formatting.DARK_GREEN);
                        message.append("+");
                        message.append(Formatting.DARK_GRAY);
                        message.append("] ");
                        message.append(Formatting.GREEN);
                        message.append(profile.getName());
                        if (this.displayGameModeOnJoin.getValue()) {
                            message.append(Formatting.DARK_GRAY);
                            message.append(" | ");
                            message.append(Formatting.DARK_AQUA);
                            String gameMode = entry.gameMode().getName();
                            gameMode = gameMode.substring(0, 1).toUpperCase() + gameMode.substring(1).toLowerCase();
                            message.append(gameMode);
                        }
                        ChatUtil.infoChatMessage(message.toString());
                    }
                }
            }
        } else if (packet instanceof final PlayerRemoveS2CPacket playerRemoveS2CPacket) {
            if (mc.getNetworkHandler() != null) {
                for (final UUID profileId : playerRemoveS2CPacket.profileIds()) {
                    final PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(profileId);
                    if (playerListEntry != null) {
                        ChatUtil.infoChatMessage(
                                Formatting.DARK_GRAY +
                                        "[" +
                                        Formatting.DARK_RED +
                                        "-" +
                                        Formatting.DARK_GRAY +
                                        "] " +
                                        Formatting.RED +
                                        playerListEntry.getProfile().getName()
                        );
                    }
                }
            }
        }
    }

}
