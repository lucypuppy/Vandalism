/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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
import de.nekosarekawaii.vandalism.event.cancellable.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.util.Formatting;

public class GameModeNotifierModule extends AbstractModule implements IncomingPacketListener {

    public GameModeNotifierModule() {
        super(
                "Game Mode Update Notifier",
                "Notifies you whenever a player updates their game mode.",
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
            if (playerListS2CPacket.getActions().contains(PlayerListS2CPacket.Action.UPDATE_GAME_MODE)) {
                for (final PlayerListS2CPacket.Entry entry : playerListS2CPacket.getEntries()) {
                    final GameProfile profile = entry.profile();
                    if (profile != null) {
                        ChatUtil.infoChatMessage(
                                Formatting.DARK_GRAY +
                                        "[" +
                                        Formatting.YELLOW +
                                        "\uD83E\uDC09" +
                                        Formatting.DARK_GRAY +
                                        "] " +
                                        Formatting.GREEN +
                                        profile.getName() +
                                        Formatting.DARK_GRAY +
                                        " | " +
                                        Formatting.GOLD +
                                        "Game Mode" +
                                        Formatting.GRAY +
                                        ": " +
                                        Formatting.DARK_AQUA +
                                        entry.gameMode().getName()
                        );
                    }
                }
            }
        }
    }

}
