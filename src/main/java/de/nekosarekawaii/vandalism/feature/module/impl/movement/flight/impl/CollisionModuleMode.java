/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.normal.network.BlockCollisionShapeListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.FlightModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;
import net.minecraft.block.Blocks;
import net.minecraft.util.shape.VoxelShapes;

public class CollisionModuleMode extends ModuleMulti<FlightModule> implements BlockCollisionShapeListener {


    public CollisionModuleMode() {
        super("Collision");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, BlockCollisionShapeEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, BlockCollisionShapeEvent.ID);
    }

    @Override
    public void onBlockCollisionShape(BlockCollisionShapeEvent event) {
        if (event.block == Blocks.AIR && event.pos.getY() < mc.player.getY()) {
            final double minX = 0, minY = 0, minZ = 0, maxX = 1, maxZ = 1;
            double maxY = 1;
            event.shape = VoxelShapes.cuboid(
                    minX,
                    minY,
                    minZ,
                    maxX,
                    maxY,
                    maxZ
            );
        }
    }
}
