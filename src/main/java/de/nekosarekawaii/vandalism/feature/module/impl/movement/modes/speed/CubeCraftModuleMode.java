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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.speed;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.entity.MotionListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.SpeedModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;
import de.nekosarekawaii.vandalism.util.minecraft.MovementUtil;
import net.minecraft.util.math.Vec3d;

public class CubeCraftModuleMode extends ModuleMulti<SpeedModule> implements MotionListener {

    public CubeCraftModuleMode(final SpeedModule parent) {
        super("Cubecraft Hop", parent);
    }

    private int offGroundTicks;
    private double moveSpeed;

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(MotionEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(MotionEvent.ID, this);
    }

    @Override
    public void onPreMotion(final MotionEvent event) {
        if ((this.mc.player.forwardSpeed != 0 || this.mc.player.sidewaysSpeed != 0) && this.mc.player.isOnGround())
            this.mc.player.jump();
    }

    @Override
    public void onPostMotion(final MotionEvent event) {
        if (this.mc.player.forwardSpeed != 0 || this.mc.player.sidewaysSpeed != 0) {
            if (this.mc.player.isOnGround()) {
                MovementUtil.setSpeed(MovementUtil.getBaseSpeed() * 1.525);
                this.moveSpeed = MovementUtil.getBaseSpeed() * 2.4;
                this.offGroundTicks = 0;
            } else {
                if (this.offGroundTicks == 0)
                    this.moveSpeed += 0.01f;
                final Vec3d velocityVector = MovementUtil.setSpeed(this.moveSpeed, this.offGroundTicks <= 2 ? 0.0026f * 45 : 0.0026f);
                final Vec3d adjustedVelocity = MovementUtil.applyFriction(velocityVector, 25);
                this.moveSpeed = Math.hypot(adjustedVelocity.getX(), adjustedVelocity.getZ());
                this.offGroundTicks++;
            }
        }
    }

}
