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
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.game.BlockCollisionShapeListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.FlightModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.MinecraftConstants;
import net.minecraft.block.Blocks;
import net.minecraft.util.shape.VoxelShapes;

public class CollisionModuleMode extends ModuleMulti<FlightModule> implements BlockCollisionShapeListener, PlayerUpdateListener {

    private int startPos;

    private final BooleanValue autoJump = new BooleanValue(
            this,
            "Auto Jump",
            "Automatically jumps to bypass dumb checks.",
            true
    );

    public CollisionModuleMode() {
        super("Collision");
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
        if (this.autoJump.getValue()) {
            if (mc.player.input.jumping && mc.player.getY() < this.startPos + 1) {
                this.startPos = (int) mc.player.getY();
            }

            if (mc.player.getY() % MinecraftConstants.MAGIC_ON_GROUND_MODULO_FACTOR == 0 && mc.player.age % 6 == 0) {
                mc.player.jump();
            }
        } else {
            this.startPos = (int) mc.player.getY();
        }
    }

    @Override
    public void onBlockCollisionShape(final BlockCollisionShapeEvent event) {
        if (event.block == Blocks.AIR && event.pos.getY() < this.startPos) {
            final double minX = 0, minY = 0, minZ = 0, maxX = 1, maxY = 1, maxZ = 1;
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
