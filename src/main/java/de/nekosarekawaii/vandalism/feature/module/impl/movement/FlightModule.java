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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.entity.MotionListener;
import de.nekosarekawaii.vandalism.base.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.flight.CreativeModuleMode;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.flight.CubeCraftModuleMode;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleModeValue;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class FlightModule extends AbstractModule implements OutgoingPacketListener, MotionListener {

    private final BooleanValue antiKick = new BooleanValue(
            this,
            "Anti Kick",
            "Bypasses the vanilla fly kick.",
            true
    );

    private final ModuleModeValue<FlightModule> mode = new ModuleModeValue<>(
            this,
            "Mode",
            "The current flight mode.",
            new CreativeModuleMode(this),
            new CubeCraftModuleMode(this)
    );

    private double lastPacketY = 0.0D;

    public FlightModule() {
        super("Flight", "Allows you to fly (even in survival or adventure).", Category.MOVEMENT);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(MotionListener.MotionEvent.ID, this);
        Vandalism.getInstance().getEventSystem().subscribe(OutgoingPacketEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(MotionListener.MotionEvent.ID, this);
        Vandalism.getInstance().getEventSystem().unsubscribe(OutgoingPacketEvent.ID, this);
    }


    @Override
    public void onPreMotion(MotionEvent event) {
        if (this.mc.player == null) return;

        if (this.antiKick.getValue()) {
            this.mc.player.ticksSinceLastPositionPacketSent = 20;
        }
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket playerMoveC2SPacket
                && this.antiKick.getValue()
                && this.mc.player.age % 2 == 0) {
            playerMoveC2SPacket.y = playerMoveC2SPacket.y - 0.1D;
        }
    }

}
