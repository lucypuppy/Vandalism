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

package de.nekosarekawaii.vandalism.event.player;

import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public interface BlockBreakListener {

    void onBlockBreak(final BlockBreakEvent event);

    class BlockBreakEvent extends CancellableEvent<BlockBreakListener> {

        public static final int ID = 40;

        public final BlockPos pos;
        public final Direction direction;
        public final BlockBreakState state;

        public BlockBreakEvent(final BlockPos pos, final Direction direction, final BlockBreakState state) {
            this.pos = pos;
            this.direction = direction;
            this.state = state;
        }

        @Override
        public void call(final BlockBreakListener listener) {
            listener.onBlockBreak(this);
        }

    }

    enum BlockBreakState {
        START,
        ABORT,
        STOP
    }

}
