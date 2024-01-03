/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.phase;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.PhaseModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;

public class FallingBlockModuleMode extends ModuleMulti<PhaseModule> implements TickGameListener {

    public FallingBlockModuleMode(final PhaseModule parent) {
        super("Falling Block", parent);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onTick() {
        if (this.mc.player == null || this.mc.world == null) return;
        if (this.mc.player.getBlockStateAtPos().isSolid() && this.mc.world.getBlockState(this.mc.player.getBlockPos().up(1)).isSolid()) {
            final double
                    yaw = Math.toRadians(this.mc.player.headYaw),
                    horizontal = this.mc.player.forwardSpeed > 0 ? 1 : this.mc.player.forwardSpeed < 0 ? -1 : 0;

            double vertical = 0;
            if (this.mc.options.sneakKey.isPressed())
                vertical = -1;
             else if (this.mc.options.jumpKey.isPressed() && this.mc.player.fallDistance < 2.0f)
                vertical = 1;

            this.mc.player.setPos(
                    this.mc.player.getX() - Math.sin(yaw) * horizontal,
                    this.mc.player.getY() + vertical,
                    this.mc.player.getZ() + Math.cos(yaw) * horizontal
            );
        }
    }

}
