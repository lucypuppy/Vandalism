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

package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.LongValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.ModeValue;
import de.nekosarekawaii.vandalism.event.game.TimeTravelListener;
import de.nekosarekawaii.vandalism.event.player.MoveInputListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.integration.rotation.hitpoint.hitpoints.entity.IcarusBHV;
import de.nekosarekawaii.vandalism.util.MSTimer;
import de.nekosarekawaii.vandalism.util.Prediction;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.profiler.Profiler;

public class LagRangeModule extends AbstractModule implements TimeTravelListener, MoveInputListener, PlayerUpdateListener {

    private long shifted, prevShifted, prevTime;
    private boolean isCharging, isDone;
    private MSTimer timer;
    private KillAuraModule killAura;

    private int ticksToShift;

    public LagRangeModule() {
        super("Lag Range", "Allows you to manipulate how minecraft handles ticks and speedup the game.", Category.COMBAT);
    }

    private final ModeValue mode = new ModeValue(this, "Mode", "The way lagrange should behave.", "Range", "On Hit");
    private final BooleanValue jump = new BooleanValue(this, "Jump", "Jumps while uncharging.", false);
    private final DoubleValue range = new DoubleValue(this, "Range", "The range to start lagging.", 3.5, 0.1, 6.0).visibleCondition(() -> mode.getValue().equalsIgnoreCase("Range"));
    private final IntegerValue hurtTime = new IntegerValue(this, "Hurt Time", "The amount of ticks to wait before lagging after attacking.", 7, 1, 10).visibleCondition(() -> mode.getValue().equalsIgnoreCase("On Hit"));

    private final IntegerValue maxCharge = new IntegerValue(this, "Max Charge", "The maximum amount of ticks you can charge.", 3, 1, 10);
    private final LongValue delay = new LongValue(this, "Delay", "The delay between lagging in milliseconds.", 1000L, 0L, 10000L);
    private final BooleanValue tickEntities = new BooleanValue(this, "Tick Entities", "Tick entities while charging.", true);
    private final BooleanValue onlyOnGround = new BooleanValue(this, "Only On Ground", "Only lag when you are on the ground.", false);
    private final BooleanValue noDamageCharge = new BooleanValue(this, "No Damage Charge", "Don't charge when receiving damage.", true);

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, TimeTravelEvent.ID, MoveInputEvent.ID, PlayerUpdateEvent.ID);
        this.timer = new MSTimer();
        this.killAura = Vandalism.getInstance().getModuleManager().getByClass(KillAuraModule.class);
        this.isCharging = false;
        this.isDone = true;
        this.ticksToShift = 0;
        isPredictedInRange = false;
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, TimeTravelEvent.ID, MoveInputEvent.ID, PlayerUpdateEvent.ID);
    }

    private boolean isPredictedInRange;

    @Override
    public void onTimeTravel(TimeTravelEvent event) {
        if (this.mc.player == null) {
            this.shifted = 0;
            return;
        }
        this.prevShifted = this.shifted;

        if (this.getCharge() >= this.maxCharge.getValue()) {
            this.isDone = false;
        }

        /* Deciding when to lag and when to uncharge */
        if (this.killAura.isActive() && this.killAura.getTarget() instanceof LivingEntity target) {
            boolean isDamaged = this.noDamageCharge.getValue() && this.mc.player.hurtTime > 2;
            switch (this.mode.getValue().toLowerCase()) {
                case "range": {
                    double distance = this.mc.player.getEyePos().distanceTo(new IcarusBHV().generateHitPoint(target));

                    if (!isCharging) {
                        this.isPredictedInRange = this.isPredictedInRange(target);
                    }

                    boolean isInRange = distance > this.killAura.getRange() && distance <= this.range.getValue();

                    this.isCharging = this.isDone && isInRange && !isDamaged && (isPredictedInRange || (this.ticksToShift > 0 && getCharge() < this.ticksToShift));
                    break;
                }
                case "on hit": {
                    boolean isPredictedInRange = false;
                    if (target.hurtTime == this.hurtTime.getValue() && !isCharging) {
                        isPredictedInRange = isPredictedInRange(target);
                    }

                    this.isCharging = this.isDone && !isDamaged && (isPredictedInRange || (this.ticksToShift > 0 && getCharge() < this.ticksToShift));
                    break;
                }
            }
        } else {
            this.isCharging = false;
        }


        /* Charging and saving the amount of shifted time */
        if (this.isCharging && this.timer.hasReached(this.delay.getValue(), false) && (!this.onlyOnGround.getValue() || this.mc.player.isOnGround())) {
            this.shifted += event.time - this.prevTime;
        }

        /* Ticking the entities so they are in sync with us */
        if (this.tickEntities.getValue() && this.prevShifted < this.shifted) {
            tickEntities();
        }

        if (this.shifted <= 0) {
            if (!this.isCharging && !this.isDone) {
                this.timer.reset();
            }
            this.isDone = true;
        }

        /* UnCharging */
        if (!this.isCharging && this.shifted > 0 && getCharge() >= this.ticksToShift) {
            this.isDone = false;
            this.shifted = 0;
            this.ticksToShift = 0;
        }

        this.prevTime = event.time;
        event.time -= this.shifted;
    }

    @Override
    public void onMoveInput(MoveInputEvent event) {
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        if (!isDone && jump.getValue() && mc.player.isOnGround()) {
            mc.player.jump();
        }
    }

    private boolean isPredictedInRange(LivingEntity target) {
        boolean isPredictedInRange = false;
        for (int ticks = Math.max(2, getCharge()); ticks <= this.maxCharge.getValue(); ++ticks) {
            LivingEntity predictedPlayer = Prediction.predictEntityMovement(this.mc.player, ticks, true, false);

            LivingEntity predictedTarget;

            if (jump.getValue()) {
                predictedTarget = Prediction.predictEntityMovement(this.mc.player, ticks, true, true);
            } else {
                predictedTarget = Prediction.predictEntityMovement(target, ticks, true, false);
            }

            double predictedDistance = predictedPlayer
                    .getEyePos()
                    .distanceTo(new IcarusBHV().generateHitPoint(predictedTarget));

            if (Math.abs(predictedDistance) <= this.killAura.getRange()) {
                isPredictedInRange = true;
                this.ticksToShift = ticks;
                break;
            }
        }
        return isPredictedInRange;
    }

    public int getCharge() {
        return (int) (this.shifted / ((RenderTickCounter.Dynamic) this.mc.getRenderTickCounter()).tickTime);
    }

    private void tickEntities() {
        Profiler profiler = this.mc.world.getProfiler();
        profiler.push("entities");
        this.mc.world.getEntities().forEach((entity) -> {
            if (entity == null || entity == this.mc.player) return;
            if (!entity.isRemoved() && !entity.hasVehicle() && !this.mc.world.getTickManager().shouldSkipTick(entity)) {
                this.mc.world.tickEntity(this.mc.world::tickEntity, entity);
            }
        });
    }
}
