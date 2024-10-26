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

package de.nekosarekawaii.vandalism.integration.clicker.clickers;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.event.game.HandleInputListener;
import de.nekosarekawaii.vandalism.feature.module.impl.combat.LagRangeModule;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.AutoSoupModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ClickerModule;
import de.nekosarekawaii.vandalism.integration.clicker.Clicker;
import de.nekosarekawaii.vandalism.util.MSTimer;
import de.nekosarekawaii.vandalism.util.PerlinNoise;
import de.nekosarekawaii.vandalism.util.math.Arithmetics;

import java.util.concurrent.ThreadLocalRandom;

public class GaussianClicker extends Clicker implements HandleInputListener {

    private final IntegerValue mean = new IntegerValue(this, "Mean CPS", "The mean clicks per second.", 12, 1, 20);
    private final IntegerValue deviation = new IntegerValue(this, "Deviation", "The deviation of the cps.", 3, 1, 20);
    private final IntegerValue minCPS = new IntegerValue(this, "Min CPS", "The minimum clicks per second limit.", 8, 1, 20);
    private final IntegerValue maxCPS = new IntegerValue(this, "Max CPS", "The maximum clicks per second limit", 15, 1, 20);
    private final IntegerValue updateChance = new IntegerValue(this, "Update Chance", "The chance to update the cps.", 80, 0, 100);
    private final IntegerValue failClickChance = new IntegerValue(this, "Fail Click Chance", "The chance to fail a click.", 0, 0, 100);
    private final IntegerValue burstChance = new IntegerValue(this, "Burst Chance", "Chance to burst-click faster for a short period.", 10, 0, 30);

    private final MSTimer clickTimer = new MSTimer();
    private long delay;
    private float partialDelays;
    private int clickCount = 0;
    private boolean isBursting = false;
    private int burstLength = 0;
    private final PerlinNoise perlinNoise = new PerlinNoise();

    public GaussianClicker(final ClickerModule clickerModule) {
        super(clickerModule, "Gaussian");

        this.mean.onValueChange((oldValue, value) -> {
            if (this.mean.getValue() > this.maxCPS.getValue()) {
                this.mean.setValue(oldValue);
            }
            if (this.mean.getValue() < this.minCPS.getValue()) {
                this.mean.setValue(oldValue);
            }
        });

        this.minCPS.onValueChange((oldValue, value) -> {
            if (this.minCPS.getValue() > this.maxCPS.getValue()) {
                this.minCPS.setValue(oldValue);
            }
        });

        this.maxCPS.onValueChange((oldValue, value) -> {
            if (this.maxCPS.getValue() < this.minCPS.getValue()) {
                this.maxCPS.setValue(oldValue);
            }
        });
    }

    @Override
    public void onActivate() {
        this.calculateCPS();
        Vandalism.getInstance().getEventSystem().subscribe(this, HandleInputListener.HandleInputEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, HandleInputListener.HandleInputEvent.ID);
    }

    @Override
    public void onHandleInputEvent(final HandleInputEvent event) {
        if (!this.clickerModule.mode.isSelected(this) || !clickerModule.shouldClick()) {
            return;
        }

        // Handle input only if auto-soup and lag-range modules are not interfering
        final LagRangeModule lagRangeModule = Vandalism.getInstance().getModuleManager().getLagRangeModule();
        final AutoSoupModule autoSoupModule = Vandalism.getInstance().getModuleManager().getAutoSoupModule();
        if (
                lagRangeModule.isActive() && lagRangeModule.noChargeHit.getValue() &&
                        (lagRangeModule.isStopAttack() || lagRangeModule.isPrevStopAttack()) || autoSoupModule.isActive()
                        && autoSoupModule.getState() != AutoSoupModule.State.WAITING
        ) {
            return;
        }

        if (ThreadLocalRandom.current().nextDouble() < this.failClickChance.getValue() * 0.01) {
            this.clickerModule.onFailClick();
            return;
        }

        if (this.clickTimer.hasReached(this.delay, true)) {
            this.clickerModule.onClick();
            this.clickCount++;

            if (!this.isBursting && ThreadLocalRandom.current().nextDouble() < this.burstChance.getValue() * 0.01) {
                this.isBursting = true;
                this.delay -= (int) (Math.random() * 60);  // Speed up for burst
                this.burstLength = 5 + ThreadLocalRandom.current().nextInt(5);  // Random burst length
            } else if (this.isBursting && this.clickCount % this.burstLength == 0) {
                this.isBursting = false;
                this.delay += (int) (Math.random() * 60);  // Slow down after burst
            }

            // Simulate fatigue by slowing down CPS after a threshold
//            if (this.clickCount >= (this.fatigueThreshold.getValue() * (0.9 + Math.random() * 0.2))) {
//                this.fatigueFactor *= (float) (0.90f + (Math.random() * 0.15f));  // More varied fatigue
//                this.delay += (int) (Math.random() * 75);  // Slightly larger delay for fatigue
//                this.clickCount = 0;
//            }

            // Random chance to recalculate CPS to introduce more variability
            if (ThreadLocalRandom.current().nextDouble() < this.updateChance.getValue() * 0.01) {
                this.calculateCPS();
            }
        }
    }

    private void calculateCPS() {
        int cps = (int) (ThreadLocalRandom.current().nextGaussian(this.mean.getValue(), this.deviation.getValue()));
        double noise = Math.abs(perlinNoise.noise(System.currentTimeMillis() / 20.0));
        cps = (int) Arithmetics.interpolate(cps - 1, cps + 1, noise);
        cps = Math.clamp(cps, this.minCPS.getValue(), this.maxCPS.getValue());

        final float delay = 1000.0F / cps;
        this.delay = (int) Math.floor(delay + this.partialDelays);
        this.partialDelays += delay - this.delay;
    }

}
