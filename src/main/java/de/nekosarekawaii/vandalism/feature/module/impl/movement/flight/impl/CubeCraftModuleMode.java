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
import de.nekosarekawaii.vandalism.event.game.TickTimeListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.FlightModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.player.MovementUtil;
import net.minecraft.util.math.Vec3d;

public class CubeCraftModuleMode extends ModuleMulti<FlightModule> implements PlayerUpdateListener, TickTimeListener {

    private int waitTicks = 0;
    private int moveTicks = 0;
    private double lastPosY = 0;
    private double moveSpeed = 0;
    private boolean canLongJump = false;
    private float timer = 1;

    public CubeCraftModuleMode() {
        super("CubeCraft");
    }

    private void reset() {
        this.waitTicks = 0;
        this.moveTicks = 0;
        this.lastPosY = 0;
        this.moveSpeed = 0;
        this.canLongJump = false;
    }

    @Override
    public void onActivate() {
        this.reset();
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, TickTimeEvent.ID);
        if (mc.getNetworkHandler() != null) {
            MovementUtil.clip(3.5, 0);
            MovementUtil.setSpeed(0.01);
            this.moveSpeed = MovementUtil.getBaseSpeed() * 3.5;
            this.lastPosY = mc.player.getY();
        }
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, TickTimeEvent.ID);
        this.reset();
    }

    @Override
    public void onPostPlayerUpdate(final PlayerUpdateEvent event) {
        if (mc.player.hurtTime > 0) {
            this.waitTicks++;
            if (this.waitTicks >= 4) {
                this.canLongJump = true;
            }
        }
        if (this.canLongJump) {
            if (mc.player.isOnGround()) {
                mc.player.setVelocity(mc.player.getVelocity().add(0, 1, 0));
            } else {
                final Vec3d moveVelocity = mc.player.getVelocity();
                if (mc.player.fallDistance > 0.2f && this.moveTicks <= 2) {
                    mc.player.setVelocity(new Vec3d(moveVelocity.getX(), 0, moveVelocity.getZ()));
                    mc.player.setVelocity(mc.player.getVelocity().add(0, 0.01, 0));
                    this.moveTicks = 5;
                    timer = 0.85f;
                    return;
                } else {
                    timer = 1.7f;
                }
                if (Math.abs(mc.player.getY() - this.lastPosY) > 1) {
                    MovementUtil.setSpeed(-0.01);
                    mc.player.fallDistance = 0;
                    mc.player.setVelocity(new Vec3d(moveVelocity.getX(), 0, moveVelocity.getZ()));
                    mc.player.setPos(mc.player.getX(), this.lastPosY, mc.player.getZ());
                    if (mc.player.input.jumping) {
                        mc.player.setPos(
                                mc.player.getX(),
                                mc.player.getY() + 0.8 + Math.random() * 0.04,
                                mc.player.getZ()
                        );
                    } else if (mc.player.input.sneaking) {
                        mc.player.setPos(
                                mc.player.getX(),
                                mc.player.getY() - 0.8 + Math.random() * 0.04,
                                mc.player.getZ()
                        );
                    }
                    this.lastPosY = mc.player.getY();
                    return;
                }
                if (mc.player.input.jumping || mc.player.input.sneaking) {
                    MovementUtil.setSpeed(0);
                    return;
                }
                final Vec3d velocityVector = MovementUtil.setSpeed(this.moveSpeed, 0.0026f);
                if (MovementUtil.getSpeed() <= 0.27) {
                    this.moveSpeed = 0.27f;
                    return;
                }
                if (mc.player.hurtTime == 0) {
                    this.moveSpeed -= 0.1f;
                }
                final Vec3d adjustedVelocity = MovementUtil.applyFriction(velocityVector, 40);
                this.moveSpeed = Math.hypot(adjustedVelocity.getX(), adjustedVelocity.getZ());
                this.moveTicks--;
            }
            if (mc.player.hurtTime >= 9) {
                MovementUtil.setSpeed(8);
            }
        }
    }

    @Override
    public void onTickTimings(TickTimeEvent event) {
        event.fromPercentage(timer);
    }

}