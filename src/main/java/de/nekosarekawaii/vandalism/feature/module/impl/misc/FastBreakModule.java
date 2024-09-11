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
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.event.player.BlockBreakListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FastBreakModule extends Module implements BlockBreakListener, PlayerUpdateListener {

    private BlockPos lastPos;
    private Direction lastDirection;

    private final IntegerValue packetsPerTick = new IntegerValue(
            this,
            "Packets Per Tick",
            "Amount of packets sent per tick.",
            5,
            2,
            10
    );

    public final FloatValue blockBreakingSpeed = new FloatValue(
            this,
            "Block Breaking Speed",
            "Speed of breaking blocks.",
            2F,
            0.0F,
            10.0F
    );

    public FastBreakModule() {
        super(
                "Fast Break",
                "Allows you to break blocks faster.",
                Category.MISC
        );
    }

    private void reset() {
        this.lastPos = null;
        this.lastDirection = null;
    }

    @Override
    public void onActivate() {
        this.reset();
        Vandalism.getInstance().getEventSystem().subscribe(
                this,
                BlockBreakEvent.ID,
                PlayerUpdateEvent.ID
        );
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(
                this,
                BlockBreakEvent.ID,
                PlayerUpdateEvent.ID
        );
        this.reset();
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        final BlockPos lastPos = this.lastPos;
        final Direction lastDirection = this.lastDirection;
        if (lastPos != null && lastDirection != null) {
            for (int i = 0; i < this.packetsPerTick.getValue(); i++) {
                this.mc.interactionManager.sendSequencedPacket(this.mc.world, sequence -> new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                        lastPos,
                        lastDirection,
                        sequence
                ));
            }
            if (this.mc.world.getBlockState(lastPos).isAir()) {
                this.reset();
            }
        }
    }

    @Override
    public void onBlockBreak(final BlockBreakEvent event) {
        if (event.state == BlockBreakState.ABORT) {
            this.reset();
            return;
        }
        this.lastPos = event.pos;
        this.lastDirection = event.direction;
    }

}
