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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.game.WorldListener;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class FlagDetectorModule extends Module implements IncomingPacketListener, WorldListener {

    private int flagCount;

    public FlagDetectorModule() {
        super("Flag Detector",
                "Detects and counts flags.",
                Category.MISC
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, IncomingPacketEvent.ID, WorldLoadEvent.ID);
        this.flagCount = 0;
    }

    @Override
    public void onDeactivate() {
        this.flagCount = 0;
        Vandalism.getInstance().getEventSystem().unsubscribe(this, IncomingPacketEvent.ID, WorldLoadEvent.ID);
    }

    @Override
    public void onIncomingPacket(IncomingPacketEvent event) {
        if (event.packet instanceof LoginHelloS2CPacket) this.flagCount = 0;
        if (mc.player == null || mc.world == null || mc.player.isDead() || mc.currentScreen instanceof LevelLoadingScreen)
            return;
        if (event.packet instanceof PlayerPositionLookS2CPacket) {
            ChatUtil.warningChatMessage("Flag detected: " + ++flagCount);
        }
    }

    @Override
    public void onPostWorldLoad() {
        this.flagCount = 0;
    }
}
