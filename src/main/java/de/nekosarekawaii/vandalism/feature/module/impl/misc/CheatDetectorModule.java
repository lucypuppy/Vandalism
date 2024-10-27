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

import de.nekosarekawaii.vandalism.event.game.WorldListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.integration.cheatdetection.detectionplayer.DetectionPlayer;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.UUID;

public class CheatDetectorModule extends Module implements WorldListener {

    private final HashMap<UUID, DetectionPlayer> detectionPlayers = new HashMap<>();

    public CheatDetectorModule() {
        super("Cheat Detector", "Detects if some player have advantages.", Category.MISC);
    }

    @Override
    public void onActivate() {
        addPlayer(mc.player);
    }

    @Override
    public void onDeactivate() {
        clearPlayers();
    }

    @Override
    public void onPostWorldLoad() {
        clearPlayers();
        addPlayer(mc.player);
    }

    private void addPlayer(final PlayerEntity player) {
        if (player == null) return;
        final UUID uuid = player.getUuid();
        final DetectionPlayer detectionPlayer = new DetectionPlayer(player);
        this.detectionPlayers.put(uuid, detectionPlayer);
        detectionPlayer.onActivate();
    }

    private void removePlayer(final PlayerEntity player) {
        if (player == null) return;
        final UUID uuid = player.getUuid();
        this.detectionPlayers.get(uuid).onDeactivate();
        this.detectionPlayers.remove(uuid);
    }

    private void clearPlayers() {
        this.detectionPlayers.forEach((uuid, detectionPlayer) -> detectionPlayer.onDeactivate());
        this.detectionPlayers.clear();
    }

}
