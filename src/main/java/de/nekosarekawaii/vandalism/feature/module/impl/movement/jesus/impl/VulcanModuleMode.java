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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.jesus.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.normal.network.BlockCollisionShapeListener;
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.jesus.JesusModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;
import de.nekosarekawaii.vandalism.util.game.MovementUtil;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

public class VulcanModuleMode extends ModuleMulti<JesusModule> implements BlockCollisionShapeListener, PlayerUpdateListener {

    public VulcanModuleMode() {
        super("Vulcan");
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
    public void onBlockCollisionShape(final BlockCollisionShapeEvent event) {
        if (event.pos.getY() < this.mc.player.getY() && !event.state.getFluidState().isEmpty()) {
            event.shape = VoxelShapes.cuboid(0, 0, 0, 1, 0.52, 1);
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (!this.mc.world.getBlockState(this.mc.player.getBlockPos()).getFluidState().isEmpty()) {
            final Vec3d velocity = MovementUtil.setSpeed(0.3);
            this.mc.player.setVelocity(velocity.x, Math.random() * 0.6, velocity.z);
            this.mc.options.sprintKey.setPressed(false);
        }
    }

}
