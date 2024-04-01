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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.normal.game.TickTimeListener;
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.game.MovementUtil;
import net.minecraft.util.math.Vec3d;

public class LongJumpModule extends AbstractModule implements PlayerUpdateListener, TickTimeListener {

    private int waitTicks = 0;
    private int moveTicks = 0;
    private double lastPosY = 0;
    private double moveSpeed = 0;
    private boolean canLongJump = false;
    private float timer = 1;

    public LongJumpModule() {
        super("Long Jump", "Lets you jump further than normal.", Category.MOVEMENT);
        this.markExperimental();
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
        if (this.mc.getNetworkHandler() != null) {
            MovementUtil.clip(3.5, 0);
            MovementUtil.setSpeed(0.01);
            this.lastPosY = this.mc.player.getY();
            this.moveSpeed = MovementUtil.getBaseSpeed() * 4;
        }
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, TickTimeEvent.ID);
        this.reset();
    }

    @Override
    public void onPostPlayerUpdate(final PlayerUpdateEvent event) {
        if (this.mc.player.hurtTime > 0) {
            this.waitTicks++;
            if (this.waitTicks >= 4) {
                this.canLongJump = true;
            }
        }
        if (this.canLongJump) {
            if (this.mc.player.isOnGround()) {
                this.mc.player.setVelocity(this.mc.player.getVelocity().add(0, 1, 0));
            } else {
                final Vec3d moveVelocity = this.mc.player.getVelocity();
                if (this.mc.player.fallDistance > 0.2f && this.moveTicks <= 2) {
                    this.mc.player.setVelocity(new Vec3d(moveVelocity.getX(), 0, moveVelocity.getZ()));
                    this.mc.player.setVelocity(this.mc.player.getVelocity().add(0, 0.01, 0));
                    this.moveTicks = 5;
                    timer = 0.8f;
                    return;
                } else {
                    timer = 1.3f;
                }
                if (Math.abs(this.mc.player.getY() - this.lastPosY) > 1) {
                    MovementUtil.setSpeed(-0.01);
                    this.mc.player.fallDistance = 0;
                    this.mc.player.setVelocity(new Vec3d(moveVelocity.getX(), 0, moveVelocity.getZ()));
                    this.mc.player.setPos(this.mc.player.getX(), this.lastPosY, this.mc.player.getZ());
                    this.lastPosY = this.mc.player.getY();
                    return;
                }
                final Vec3d velocityVector = MovementUtil.setSpeed(this.moveSpeed, 0.0026f);
                if (MovementUtil.getSpeed() <= 0.27) {
                    this.moveSpeed = 0.27f;
                    return;
                }
                if (this.mc.player.hurtTime == 0) {
                    this.moveSpeed -= 0.1f;
                }
                final Vec3d adjustedVelocity = MovementUtil.applyFriction(velocityVector, 40);
                this.moveSpeed = Math.hypot(adjustedVelocity.getX(), adjustedVelocity.getZ());
                this.moveTicks--;
            }
        }
    }

    @Override
    public void onTickTimings(TickTimeEvent event) {
        event.fromPercentage(timer);
    }

}
