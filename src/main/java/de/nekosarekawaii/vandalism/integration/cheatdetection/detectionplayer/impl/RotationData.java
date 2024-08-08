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

package de.nekosarekawaii.vandalism.integration.cheatdetection.detectionplayer.impl;

import de.florianmichael.dietrichevents2.Priorities;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.integration.cheatdetection.detectionplayer.DetectionPlayer;
import de.nekosarekawaii.vandalism.integration.cheatdetection.detectionplayer.PlayerData;
import de.nekosarekawaii.vandalism.util.math.MathUtil;
import lombok.Getter;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@Getter
public class RotationData extends PlayerData implements OutgoingPacketListener {

    private float yaw, pitch, lastYaw, lastPitch;
    private float yawDelta, pitchDelta, lastYawDelta, lastPitchDelta;
    private float yawAccel, pitchAccel, lastYawAccel, lastPitchAccel;
    private double yawGcd, pitchGcd,  lastPitchGcd, lastYawGcd;

    public RotationData(final DetectionPlayer player) {
        super(player);
    }
    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(OutgoingPacketEvent.ID, this, Priorities.HIGH);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(OutgoingPacketEvent.ID, this);
    }

    @Override
    public void onOutgoingPacket(OutgoingPacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket packet){
            this.lastYaw = this.yaw;
            this.lastPitch = this.pitch;
            this.yaw = packet.yaw;
            this.pitch = packet.pitch;

            this.lastYawDelta = this.yawDelta;
            this.lastPitchDelta = this.pitchDelta;
            this.yawDelta = Math.abs(this.yaw - this.lastYaw);
            this.pitchDelta = Math.abs(this.pitch - this.lastPitch);

            this.lastYawAccel = this.yawAccel;
            this.lastPitchAccel = this.pitchAccel;
            this.yawAccel = Math.abs(this.yawDelta - this.lastYawDelta);
            this.pitchAccel = Math.abs(this.pitchDelta - this.lastPitchDelta);

            this.lastYawGcd = this.yawGcd;
            this.lastPitchGcd = this.pitchGcd;
            this.yawGcd = MathUtil.getAbsoluteGcd(this.yawDelta, this.lastYawDelta);
            this.pitchGcd = MathUtil.getAbsoluteGcd(this.pitchDelta, this.lastPitchDelta);
        }
    }

}
