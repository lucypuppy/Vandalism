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
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.game.TimeTravelListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.integration.rotation.hitpoint.hitpoints.entity.IcarusBHV;
import de.nekosarekawaii.vandalism.util.Prediction;
import de.nekosarekawaii.vandalism.util.WorldUtil;
import lombok.Getter;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public class LagRangeModule extends Module implements TimeTravelListener, PlayerUpdateListener {

    private final IntegerValue tickLimit = new IntegerValue(this, "Tick Limit", "The maximum amount of ticks you can charge.", 3, 1, 10);

    private final IntegerValue ticksToWait = new IntegerValue(this, "Ticks to wait", "Ticks to wait before being able to charge again.", 10, 0, 20);

    private final BooleanValue onlyOnGround = new BooleanValue(this, "Only On Ground", "Only charge when you are on the ground.", false);

    public final BooleanValue noChargeHit = new BooleanValue(this, "No Charge Hit", "Don't hit while charging.", true);

    private State state = State.IDLE;
    private long shifted, prevShifted;
    private boolean canShift;

    @Getter
    private boolean stopAttack, prevStopAttack;
    private float limit = 0;
    private int ticksWaited = 0;

    public LagRangeModule() {
        super("Lag Range", "Allows you to manipulate how minecraft handles ticks and speedup the game.", Category.COMBAT);
    }

    private void reset() {
        this.limit = 0;
        this.ticksWaited = 0;
        this.state = State.IDLE;
        this.canShift = false;
        this.prevStopAttack = false;
        this.stopAttack = false;
    }

    @Override
    protected void onActivate() {
        this.reset();
        Vandalism.getInstance().getEventSystem().subscribe(this, TimeTravelEvent.ID, PlayerUpdateEvent.ID);
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, TimeTravelEvent.ID, PlayerUpdateEvent.ID);
        this.reset();
    }

    @Override
    public void onTimeTravel(final TimeTravelEvent event) {
        if (mc.player == null) {
            this.shifted = 0;
            return;
        }

        if (mc.player.age % 2 == 0) {
            this.prevStopAttack = this.stopAttack;
        }

        final KillAuraModule killAuraModule = Vandalism.getInstance().getModuleManager().getKillAuraModule();
        if (killAuraModule.isActive() && killAuraModule.getTarget() instanceof final LivingEntity target) {
            this.dynamicShit(target);
        }

        if (this.canShift) {
            if (this.getCharge() <= this.limit) {
                this.stopAttack = true;
                this.state = State.CHARGING;
            } else {
                this.prevStopAttack = this.stopAttack;
                this.stopAttack = false;
                this.ticksWaited = 0;
                this.canShift = false;
                this.state = State.UNCHARGING;
            }
        }

        switch (this.state) {
            case CHARGING -> this.shifted += event.time - this.prevShifted;
            case UNCHARGING -> {
                if (this.shifted > 0) {
                    this.shifted = 0;
                } else {
                    this.state = State.IDLE;
                }
            }
            default -> {
            }
        }

        this.prevShifted = event.time;
        event.time -= this.shifted;
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        final KillAuraModule killAuraModule = Vandalism.getInstance().getModuleManager().getKillAuraModule();
        if (this.state == State.IDLE && killAuraModule.getTarget() != null) {
            this.ticksWaited++;
        }
    }

    private enum State {
        IDLE,
        CHARGING,
        UNCHARGING
    }

    private int getCharge() {
        return (int) (this.shifted / ((RenderTickCounter.Dynamic) mc.getRenderTickCounter()).tickTime);
    }

    private void dynamicShit(final LivingEntity target) {
        for (int i = 2; i <= this.tickLimit.getValue(); i++) {
            this.limit = (float) (i + (int) (Math.random() * 1.55f));

            final LivingEntity predictedTarget = Prediction.predictEntityMovement(target, (int) this.limit, true, false);
            final LivingEntity currentPredictedTarget = Prediction.predictEntityMovement(target, getCharge(), true, false);

            final LivingEntity predictedPlayer = Prediction.predictEntityMovement(mc.player, (int) this.limit, true, false);
            final Vec3d targetBHV = new IcarusBHV().generateHitPoint(predictedTarget);
            final double playerRange = WorldUtil.calculateRange(predictedPlayer.getEyePos(), targetBHV);

            final Vec3d playerBHV = new IcarusBHV().generateHitPoint(predictedPlayer);
            final double targetRange = WorldUtil.calculateRange(predictedTarget.getEyePos(), playerBHV);

            final Vec3d currentPlayerBHV = new IcarusBHV().generateHitPoint(mc.player);
            final double currentTargetRange = WorldUtil.calculateRange(currentPredictedTarget.getEyePos(), currentPlayerBHV);

            final boolean inRange = playerRange <= 3 && playerRange > 0 && (targetRange > 3 && currentTargetRange > 3);

            if (inRange && this.ticksWaited >= this.ticksToWait.getValue() && mc.player.hurtTime - this.limit <= 7 && (!this.onlyOnGround.getValue() || mc.player.isOnGround())) {
                this.canShift = true;
                break;
            }
        }
    }

}
