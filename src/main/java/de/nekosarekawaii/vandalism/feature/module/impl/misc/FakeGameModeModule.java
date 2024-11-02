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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.selection.ModeValue;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.WorldUtil;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.world.GameMode;

import java.util.Arrays;

public class FakeGameModeModule extends Module implements PlayerUpdateListener, OutgoingPacketListener {

    private final ModeValue mode = new ModeValue(
            this,
            "Mode",
            "The game mode you want to set.",
            Arrays.stream(GameMode.values()).map(gameMode -> gameMode.getName() + " (" + gameMode.getId() + ")").toArray(String[]::new)
    );

    public FakeGameModeModule() {
        super("Fake Game Mode", "Allows you to set your clientside game mode.", Category.MISC);
    }

    private void reset() {
        final GameMode gameMode = WorldUtil.getGameMode(mc.getGameProfile().getId());
        if (gameMode != null) {
            mc.interactionManager.setGameMode(gameMode);
        }
    }

    @Override
    public void onActivate() {
        this.reset();
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, OutgoingPacketEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, OutgoingPacketEvent.ID);
        this.reset();
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        for (final GameMode gameMode : GameMode.values()) {
            if (this.mode.getValue().startsWith(gameMode.getName())) {
                mc.interactionManager.setGameMode(gameMode);
                break;
            }
        }
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        if (event.packet instanceof CreativeInventoryActionC2SPacket) {
            final GameMode serverSideGameMode = WorldUtil.getGameMode(mc.getGameProfile().getId());
            if (serverSideGameMode != null) {
                final GameMode clientSideGameMode = mc.interactionManager.getCurrentGameMode();
                if (clientSideGameMode == GameMode.CREATIVE && serverSideGameMode != clientSideGameMode) {
                    event.cancel();
                    ChatUtil.errorChatMessage("You are not allowed to use creative mode in survival.");
                }
            }
        }
    }

}
