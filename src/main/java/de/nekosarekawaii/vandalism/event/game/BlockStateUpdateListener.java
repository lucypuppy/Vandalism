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

package de.nekosarekawaii.vandalism.event.game;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface BlockStateUpdateListener {

    void onBlockStateUpdate(final BlockPos pos, final BlockState previousState, final BlockState state);

    class BlockStateUpdateEvent extends AbstractEvent<BlockStateUpdateListener> {

        public static final int ID = 43;

        private final BlockPos pos;
        private final BlockState previousState;
        private final BlockState state;

        public BlockStateUpdateEvent(final BlockPos pos, final BlockState previousState, final BlockState state) {
            this.pos = pos;
            this.previousState = previousState;
            this.state = state;
        }

        @Override
        public void call(final BlockStateUpdateListener listener) {
            listener.onBlockStateUpdate(this.pos, this.previousState, this.state);
        }

    }

}
