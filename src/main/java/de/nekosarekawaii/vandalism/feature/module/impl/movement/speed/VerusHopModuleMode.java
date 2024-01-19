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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.speed;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;
import de.nekosarekawaii.vandalism.util.game.MovementUtil;
import net.minecraft.util.math.Vec3d;

public class VerusHopModuleMode extends ModuleMulti<SpeedModule> implements PlayerUpdateListener {

    private int offGroundTicks = 0;
    private double moveSpeed = 0;

    public VerusHopModuleMode() {
        super("Verus Hop");
    }

    private void reset() {
        this.offGroundTicks = 0;
        this.moveSpeed = 0;
    }

    @Override
    public void onActivate() {
        this.reset();
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
        this.reset();
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (MovementUtil.isMoving() && this.mc.player.isOnGround()) {
            this.mc.player.jump();
        }
    }

    @Override
    public void onPostPlayerUpdate(final PlayerUpdateEvent event) {
        if (this.mc.player.isOnGround()) {
            MovementUtil.setSpeed(MovementUtil.getBaseSpeed() * 1.525);
            this.moveSpeed = MovementUtil.getBaseSpeed() * 2.4;
            this.offGroundTicks = 0;
        } else {
            if (this.offGroundTicks == 0) {
                this.moveSpeed += 0.01f;
            }
            final Vec3d velocityVector = MovementUtil.setSpeed(this.moveSpeed, this.offGroundTicks <= 2 ? 0.0026f * 45 : 0.0026f);
            final Vec3d adjustedVelocity = MovementUtil.applyFriction(velocityVector, (float) (Math.random() * 1E-5));
            this.moveSpeed = Math.hypot(adjustedVelocity.getX(), adjustedVelocity.getZ());
            this.offGroundTicks++;
        }
    }

}
