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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.speed.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.speed.SpeedModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.player.MovementUtil;
import net.minecraft.util.math.Vec3d;

public class CubeCraftModuleMode extends ModuleMulti<SpeedModule> implements PlayerUpdateListener {

    private int offGroundTicks = 0;
    private double moveSpeed = 0;

    public CubeCraftModuleMode() {
        super("CubeCraft Hop");
    }

    private void reset() {
        this.offGroundTicks = 0;
        this.moveSpeed = 0;
    }

    @Override
    public void onActivate() {
        this.reset();
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateListener.PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateListener.PlayerUpdateEvent.ID, this);
        this.reset();
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateListener.PlayerUpdateEvent event) {
        if (MovementUtil.isMoving() && this.mc.player.isOnGround()) {
            this.mc.player.jump();
        }
    }

    @Override
    public void onPostPlayerUpdate(final PlayerUpdateListener.PlayerUpdateEvent event) {
        if (this.mc.player.isOnGround()) {
            MovementUtil.setSpeed(MovementUtil.getBaseSpeed() * 1.525);
            this.moveSpeed = MovementUtil.getBaseSpeed() * 2.4;
            this.offGroundTicks = 0;
        } else {
            if (this.offGroundTicks == 0) {
                this.moveSpeed += 0.01f;
            }
            final Vec3d velocityVector = MovementUtil.setSpeed(this.moveSpeed, this.offGroundTicks <= 2 ? 0.0026f * 45 : 0.0026f);
            final Vec3d adjustedVelocity = MovementUtil.applyFriction(velocityVector, 25);
            this.moveSpeed = Math.hypot(adjustedVelocity.getX(), adjustedVelocity.getZ());
            this.offGroundTicks++;
        }
    }

}
