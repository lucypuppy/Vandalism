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

package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import de.florianmichael.rclasses.math.timer.MSTimer;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.LongValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.normal.game.TimeTravelListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class LagRangeModule extends AbstractModule implements TimeTravelListener {

    private long shifted, prevShifted, prevTime;
    private boolean isCharging, isUnCharging, isDone;
    private MSTimer timer;
    private KillAuraModule killAura;

    public LagRangeModule() {
        super("Lag Range", "Allows you to manipulate how minecraft handles ticks and speedup the game.", Category.COMBAT);
    }

    private DoubleValue range = new DoubleValue(this, "Range", "The range to start lagging.", 3.5, 0.1, 6.0);
    private IntegerValue maxCharge = new IntegerValue(this, "Max Charge", "The maximum amount of ticks you can charge.", 3, 1, 10);
    private LongValue delay = new LongValue(this, "Delay", "The delay between lagging in milliseconds.", 1000L, 0L, 10000L);
    private BooleanValue tickEntities = new BooleanValue(this, "Tick Entities", "Tick entities while charging.", true);

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, TimeTravelEvent.ID);
        timer = new MSTimer();
        this.killAura = Vandalism.getInstance().getModuleManager().getByClass(KillAuraModule.class);
        isCharging = isUnCharging = false;
        isDone = true;
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, TimeTravelEvent.ID);
    }

    @Override
    public void onTimeTravel(TimeTravelEvent event) {
        if (mc.player == null) {
            shifted = 0;
            return;
        }
        prevShifted = shifted;

        if(this.getCharge() >= maxCharge.getValue()) {
            isDone = false;
        }

        /* Deciding when to lag and when to uncharge */
        if(killAura.isActive() && killAura.getTarget() != null) {
            Entity target = killAura.getTarget();
            Vec3d eyePos = mc.player.getEyePos();
//            Vec3d predictedPos = predictFuturePosition(target, this.getCharge());
            Vec3d predictedEyePos = predictFutureEyePosition(mc.player, this.getCharge());
            double distance = eyePos.distanceTo(target.getPos());
//            double predictedDistance = eyePos.distanceTo(predictedPos);
            double predictedDistance = predictedEyePos.distanceTo(target.getPos());
            if(distance > killAura.getRange() && distance <= range.getValue()) {
                if(isDone) {
                    if(predictedDistance > killAura.getRange()) {
                        this.isCharging = true;
                        this.isUnCharging = false;
                    } else if(getCharge() > 0) {
                        this.isCharging = false;
                        this.isUnCharging = true;
                    }
                } else {
                    this.isCharging = false;
                    this.isUnCharging = true;
                }
            } else {
                this.isCharging = false;
                this.isUnCharging = false;
            }
        } else {
            this.isCharging = false;
            this.isUnCharging = false;
        }

        /* Charging and saving the amount of shifted time */
        if (!isUnCharging) {
            if (isCharging && timer.hasReached(delay.getValue(), false) && getCharge() < maxCharge.getValue()) {
                shifted += event.time - prevTime;
            }
        }

        /* Ticking the entities so they are in sync with us */
        if (tickEntities.getValue() && prevShifted < shifted) {
            tickEntities();
        }

        /* UnCharging */
        if(isUnCharging) {
            if(getCharge() > 0) {
                isDone = false;
                shifted = 0;
            }
        }

        if(this.getCharge() <= 0 || !isUnCharging) {
            if(!isCharging && !isDone) {
                timer.reset();
            }
            isDone = true;
        }

        prevTime = event.time;
        event.time -= shifted;
    }

    public int getCharge() {
        return (int) (shifted / mc.renderTickCounter.tickTime);
    }

    private void tickEntities() {
        for (Entity entity : mc.world.getEntities()) {
            if (entity == null || entity == mc.player) continue;
            mc.world.tickEntity(entity);
        }
    }

    public Vec3d predictFuturePosition(Entity entity, int ticksInFuture) {
        Vec3d currentPosition = entity.getPos();
        Vec3d velocity = new Vec3d(entity.getVelocity().x, entity.getVelocity().y, entity.getVelocity().z);

        // Predict future position based on current velocity
        Vec3d futurePosition = currentPosition.add(velocity.multiply(ticksInFuture));

        return futurePosition;
    }

    public Vec3d predictFutureEyePosition(Entity entity, int ticksInFuture) {
        Vec3d currentPosition = entity.getEyePos();
        Vec3d velocity = new Vec3d(entity.getVelocity().x, entity.getVelocity().y, entity.getVelocity().z);

        // Predict future position based on current velocity
        Vec3d futurePosition = currentPosition.add(velocity.multiply(ticksInFuture));

        return futurePosition;
    }
}
