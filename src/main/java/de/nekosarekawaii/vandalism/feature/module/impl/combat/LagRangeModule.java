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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.LongValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.normal.game.TimeTravelListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.common.MSTimer;
import de.nekosarekawaii.vandalism.util.game.Prediction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.profiler.Profiler;

public class LagRangeModule extends AbstractModule implements TimeTravelListener {

    private long shifted, prevShifted, prevTime;
    private boolean isCharging, isDone;
    private MSTimer timer;
    private KillAuraModule killAura;

    public LagRangeModule() {
        super("Lag Range", "Allows you to manipulate how minecraft handles ticks and speedup the game.", Category.COMBAT);
    }

    private DoubleValue range = new DoubleValue(this, "Range", "The range to start lagging.", 3.5, 0.1, 6.0);
    private IntegerValue maxCharge = new IntegerValue(this, "Max Charge", "The maximum amount of ticks you can charge.", 3, 1, 10);
    private LongValue delay = new LongValue(this, "Delay", "The delay between lagging in milliseconds.", 1000L, 0L, 10000L);
    private BooleanValue tickEntities = new BooleanValue(this, "Tick Entities", "Tick entities while charging.", true);
    private BooleanValue onlyOnGround = new BooleanValue(this, "Only On Ground", "Only lag when you are on the ground.", false);

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, TimeTravelEvent.ID);
        timer = new MSTimer();
        this.killAura = Vandalism.getInstance().getModuleManager().getByClass(KillAuraModule.class);
        isCharging = false;
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

        if (this.getCharge() >= maxCharge.getValue()) {
            isDone = false;
        }

        /* Deciding when to lag and when to uncharge */
        if (killAura.isActive() && killAura.getTarget() instanceof LivingEntity target) {
            double distance = mc.player.getEyePos().distanceTo(target.getPos());

            boolean isPredictedInRange = false;

            for (int ticks = Math.min(getCharge(), 1); ticks <= maxCharge.getValue(); ++ticks) {
                double predictedDistance = Prediction.predictEntityMovement(mc.player, ticks, true)
                        .add(0, mc.player.getStandingEyeHeight(), 0)
                        .distanceTo(Prediction.predictEntityMovement(target, ticks, true));
                if (predictedDistance <= killAura.getRange()) {
                    isPredictedInRange = true;
                    break;
                }
            }

            boolean isInRange = distance > killAura.getRange() && distance <= range.getValue();

            this.isCharging = isDone && isInRange && isPredictedInRange;
        } else {
            this.isCharging = false;
        }


        /* Charging and saving the amount of shifted time */
        if (isCharging && timer.hasReached(delay.getValue(), false) && (!onlyOnGround.getValue() || mc.player.isOnGround())) {
            shifted += event.time - prevTime;
        }

        /* Ticking the entities so they are in sync with us */
        if (tickEntities.getValue() && prevShifted < shifted) {
            tickEntities();
        }

        /* UnCharging */
        if (!isCharging && shifted > 0) {
            isDone = false;
            shifted = 0;
        }

        if (shifted <= 0) {
            if (!isCharging && !isDone) {
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

        Profiler profiler = mc.world.getProfiler();
        profiler.push("entities");
        mc.world.getEntities().forEach((entity) -> {
            if (entity == null || entity == mc.player) return;
            if (!entity.isRemoved() && !entity.hasVehicle() && !mc.world.getTickManager().shouldSkipTick(entity)) {
                mc.world.tickEntity(mc.world::tickEntity, entity);
            }
        });
    }
}
