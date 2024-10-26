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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.jesus.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.game.BlockCollisionShapeListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.jesus.JesusModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.util.shape.VoxelShapes;

public class StaticModuleMode extends ModuleMulti<JesusModule> implements BlockCollisionShapeListener {

    private final BooleanValue disableOnSneak = new BooleanValue(
            this,
            "Disable on Sneak",
            "Whether or not to disable this module when you are sneaking.",
            true
    );

    public StaticModuleMode() {
        super("Static");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(BlockCollisionShapeEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(BlockCollisionShapeEvent.ID, this);
    }

    @Override
    public void onBlockCollisionShape(final BlockCollisionShapeEvent event) {
        if (this.disableOnSneak.getValue() && this.mc.player.input.sneaking || (this.mc.player != null && this.mc.player.hasVehicle())) {
            return;
        }
        final BlockState state = event.state;
        final FluidState fluidState = state.getFluidState();
        if (event.pos.getY() < this.mc.player.getY() && !fluidState.isEmpty()) {
            final double minX = 0, minY = 0, minZ = 0, maxX = 1, maxZ = 1;
            double maxY = 1;
            if (fluidState.getFluid() instanceof WaterFluid && (this.mc.player.isOnFire() || this.mc.player.fallDistance >= 2)) {
                maxY = 0.59;
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
