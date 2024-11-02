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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.selection.ModeValue;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.event.player.MoveInputListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.NoFallModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;


//Todo only need to bypass the OnGround Flags
public class VulcanModuleMode extends ModuleMulti<NoFallModule> implements OutgoingPacketListener, MoveInputListener {

    private boolean stopMove;
    private boolean fixFlag;
    private int airTicks;

    private final double[] vulcanStepCValues = new double[]{
            -0.0785000026226043,
            -0.1553000062704086,
            -0.2305999994277954,
            -0.3043999969959259,
            -0.376800000667572,
            -0.4474999904632568,
            -0.5170999765396118,
            -0.5860000252723694,
            -0.6520000100135803,
            -0.7171000242233276,
    };

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
        this.stopMove = false;
        this.fixFlag = false;
    }


    @Override
    public void onOutgoingPacket(final OutgoingPacketListener.OutgoingPacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket playerPacket) {
            if (!mc.player.isOnGround()) {
                if (this.airTicks < this.vulcanStepCValues.length - 1) {
                    this.airTicks++;
                }
            } else {
                this.airTicks = 0;
            }

            if (mc.player.fallDistance > 3.0f) {
                if (isAirStop()) {
                    mc.player.setVelocity(0, -0.1, 0);
                    this.stopMove = true;
                    this.fixFlag = true;
                }

                playerPacket.onGround = true;
                this.airTicks = 0;
                mc.player.fallDistance = 0;
            } else if (this.fixFlag) {
                mc.player.setVelocity(0, this.vulcanStepCValues[this.airTicks] + 0.05, 0);
                this.fixFlag = false;
            }
        }
    }

    @Override
    public void onMoveInput(MoveInputEvent event) {
        if (this.stopMove && isAirStop()) {
            event.cancel();
            this.stopMove = false;
        }
    }

    private boolean isAirStop() {
        return mode.getValue().equalsIgnoreCase("Air Stop");
    }
}
