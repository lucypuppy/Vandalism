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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.game.BlockCollisionShapeListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.FlightModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.MinecraftConstants;
import de.nekosarekawaii.vandalism.util.player.MovementUtil;
import de.nekosarekawaii.vandalism.util.player.PlayerUtil;
import net.minecraft.block.Blocks;
import net.minecraft.util.shape.VoxelShapes;

public class NegativityV2ModuleMode extends ModuleMulti<FlightModule> implements BlockCollisionShapeListener, PlayerUpdateListener {

    private int startPos;

    public NegativityV2ModuleMode() {
        super("Negativity V2");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, BlockCollisionShapeEvent.ID, PlayerUpdateEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, BlockCollisionShapeEvent.ID, PlayerUpdateEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        mc.options.jumpKey.setPressed(false);
        if (MovementUtil.isMoving() && !PlayerUtil.isOnGround(0.1)) {
            MovementUtil.setSpeed(0.65);
        }
        this.startPos = (int) mc.player.getY();
    }

    @Override
    public void onBlockCollisionShape(final BlockCollisionShapeEvent event) {
        if (event.block == Blocks.AIR && event.pos.getY() < this.startPos) {
            final double minX = 0, minY = 0, minZ = 0, maxX = 1, maxY = 1, maxZ = 1;
            if (mc.player.getY() % MinecraftConstants.MAGIC_ON_GROUND_MODULO_FACTOR <= 0.2 && mc.player.age % 7 == 0) {
                return;
            }
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
