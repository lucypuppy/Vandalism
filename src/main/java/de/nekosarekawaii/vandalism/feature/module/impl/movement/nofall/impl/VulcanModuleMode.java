/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.selection.ModeValue;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.event.player.MoveInputListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.NoFallModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class VulcanModuleMode extends ModuleMulti<NoFallModule> implements OutgoingPacketListener, MoveInputListener {

    private boolean stopMove;

    public VulcanModuleMode() {
        super("Vulcan");
    }

    private final ModeValue mode = new ModeValue(this, "Mode", "The mode of the no fall.", "Spoof", "Air Stop");

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, OutgoingPacketEvent.ID, MoveInputEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, OutgoingPacketEvent.ID, MoveInputEvent.ID);
        stopMove = false;
    }


    @Override
    public void onOutgoingPacket(final OutgoingPacketListener.OutgoingPacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket playerPacket && this.mc.player.fallDistance > 3.0f) {
            if (isAirStop()) {
                mc.player.setVelocity(0, -0.029, 0);
                stopMove = true;
            }
            playerPacket.onGround = true;
            mc.player.fallDistance = 0;
        }
    }

    @Override
    public void onMoveInput(MoveInputEvent event) {
        if (stopMove && isAirStop()) {
            event.cancel();
            stopMove = false;
        }
    }

    private boolean isAirStop() {
        return mode.getValue().equalsIgnoreCase("Air Stop");
    }
}
