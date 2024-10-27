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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.event.game.BlockCollisionShapeListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.FlightModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.player.MovementUtil;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

public class CubeCraft2ModuleMode extends ModuleMulti<FlightModule> implements PlayerUpdateListener, BlockCollisionShapeListener {

    private final DoubleValue motionYOffset = new DoubleValue(
            this,
            "Motion Y Offset",
            "The motion y offset of the motion flight.",
            0.2,
            0.0,
            10.0
    );

    private final DoubleValue speed = new DoubleValue(
            this,
            "Speed",
            "The speed amount of the motion flight.",
            1.0,
            0.0,
            10.0
    );

    public CubeCraft2ModuleMode() {
        super("CubeCraft 2");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, BlockCollisionShapeEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, BlockCollisionShapeEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        double motionX = 0;
        double motionZ = 0;
        final double motionY = mc.player.input.jumping ? motionYOffset.getValue() : mc.player.input.sneaking ? -motionYOffset.getValue() : 0;
        if (MovementUtil.isMoving()) {
            final Vec3d speedVelocity = MovementUtil.setSpeed(speed.getValue());
            motionX = speedVelocity.x;
            motionZ = speedVelocity.z;
        }
        mc.player.setVelocity(0, 0, 0);
        mc.player.setPos(mc.player.getX() + motionX, mc.player.getY() + motionY, mc.player.getZ() + motionZ);
    }

    @Override
    public void onBlockCollisionShape(BlockCollisionShapeEvent event) {
        if (event.block == Blocks.AIR) {
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
