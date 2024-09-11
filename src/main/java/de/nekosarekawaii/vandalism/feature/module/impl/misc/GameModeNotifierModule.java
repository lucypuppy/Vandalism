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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import com.mojang.authlib.GameProfile;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.game.WorldListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.List;

public class GameModeNotifierModule extends Module implements WorldListener, PlayerUpdateListener {

    private final List<PlayerEntry> playerEntries = new ArrayList<>();

    public GameModeNotifierModule() {
        super(
                "Game Mode Notifier",
                "Notifies you whenever a player updates their game mode.",
                Category.MISC
        );
    }

    @Override
    public void onActivate() {
        this.playerEntries.clear();
        Vandalism.getInstance().getEventSystem().subscribe(this, WorldLoadEvent.ID, PlayerUpdateEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, WorldLoadEvent.ID, PlayerUpdateEvent.ID);
        this.playerEntries.clear();
    }

    @Override
    public void onPostWorldLoad() {
        this.playerEntries.clear();
    }

    @Override
    public void onPostPlayerUpdate(final PlayerUpdateEvent event) {
        if (this.mc.getNetworkHandler() == null) return;
        for (final PlayerListEntry playerListEntry : this.mc.getNetworkHandler().getPlayerList()) {
            final GameProfile profile = playerListEntry.getProfile();
            if (profile != null) {
                final GameMode gameMode = playerListEntry.getGameMode();
                if (gameMode != null) {
                    PlayerEntry entry = null;
                    boolean found = false;
                    for (final PlayerEntry playerEntry : this.playerEntries) {
                        if (playerEntry.getName().equals(profile.getName())) {
                            playerEntry.setCurrentGameMode(gameMode);
                            entry = playerEntry;
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        final PlayerEntry playerEntry = new PlayerEntry(profile.getName());
                        playerEntry.setCurrentGameMode(gameMode);
                        this.playerEntries.add(playerEntry);
                        entry = playerEntry;
                    }
                    if (entry.hasBeenUpdated()) {
                        final String name = entry.getName();
                        final StringBuilder notification = new StringBuilder();
                        notification.append(Formatting.DARK_GRAY);
                        notification.append("[");
                        notification.append(Formatting.YELLOW);
                        notification.append("\uD83E\uDC09");
                        notification.append(Formatting.DARK_GRAY);
                        notification.append("] ");
                        notification.append(Formatting.GREEN);
                        notification.append(name);
                        notification.append(Formatting.DARK_GRAY);
                        notification.append(" | ");
                        if (entry.getLastGameMode() != null) {
                            notification.append(Formatting.RED);
                            String lastGameModeName = entry.getLastGameMode().getName();
                            lastGameModeName = lastGameModeName.substring(0, 1).toUpperCase() + lastGameModeName.substring(1).toLowerCase();
                            notification.append(lastGameModeName);
                            notification.append(Formatting.GRAY);
                            notification.append(" > ");
                        }
                        notification.append(Formatting.DARK_AQUA);
                        String currentGameModeName = gameMode.getName();
                        currentGameModeName = currentGameModeName.substring(0, 1).toUpperCase() + currentGameModeName.substring(1).toLowerCase();
                        notification.append(currentGameModeName);
                        ChatUtil.infoChatMessage(notification.toString());
                        entry.setLastGameMode(entry.getCurrentGameMode());
                    }
                }
            }
        }
    }

    @Getter
    private static class PlayerEntry {

        private final String name;

        @Setter
        private GameMode lastGameMode, currentGameMode;

        public PlayerEntry(final String name) {
            this.name = name;
        }

        public boolean hasBeenUpdated() {
            return this.lastGameMode == null || !this.lastGameMode.equals(this.currentGameMode);
        }

    }

}
