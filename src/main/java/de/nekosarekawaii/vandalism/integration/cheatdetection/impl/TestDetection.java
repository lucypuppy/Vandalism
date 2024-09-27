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

package de.nekosarekawaii.vandalism.integration.cheatdetection.impl;

import de.florianmichael.dietrichevents2.Priorities;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.integration.cheatdetection.Detection;
import de.nekosarekawaii.vandalism.integration.cheatdetection.detectionplayer.DetectionPlayer;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class TestDetection extends Detection implements OutgoingPacketListener {

    public TestDetection(DetectionPlayer player) {
        super(player, "Test");
        setExperimental();
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(OutgoingPacketEvent.ID, this, Priorities.HIGHEST);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(OutgoingPacketEvent.ID, this);
    }

    @Override
    public void onOutgoingPacket(OutgoingPacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket packet) {
            final double pitchGCD = player.getRotationData().getPitchGcd();


            if (pitchGCD >= 2.47437088E8) {
              // verbose("Pitch GCD: " + pitchGCD);
            }

            //System.out.println("Pitch GCD: " + pitchGCD);
        }
    }

}
