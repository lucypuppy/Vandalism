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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public interface BlockCollisionShapeListener {

    void onBlockCollisionShape(final BlockCollisionShapeEvent event);

    class BlockCollisionShapeEvent extends AbstractEvent<BlockCollisionShapeListener> {

        public static final int ID = 13;

        public final Block block;
        public final BlockState state;
        public final BlockView world;
        public final BlockPos pos;
        public final ShapeContext context;
        public VoxelShape shape;

        public BlockCollisionShapeEvent(final Block block, final BlockState state, final BlockView world, final BlockPos pos, final ShapeContext context, final VoxelShape shape) {
            this.block = block;
            this.state = state;
            this.world = world;
            this.pos = pos;
            this.context = context;
            this.shape = shape;
        }

        @Override
        public void call(final BlockCollisionShapeListener listener) {
            listener.onBlockCollisionShape(this);
        }

    }

}
