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
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.combat.LagRangeModule;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.AutoSoupModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ClickerModule;
import de.nekosarekawaii.vandalism.integration.clicker.Clicker;
import de.nekosarekawaii.vandalism.util.math.Arithmetics;

import java.util.concurrent.ThreadLocalRandom;

public class TestClicker extends Clicker implements PlayerUpdateListener {

    private final IntegerValue minCPS = new IntegerValue(this, "Min CPS", "The minimum clicks per second limit.", 8, 5, 20);
    private final IntegerValue maxCPS = new IntegerValue(this, "Max CPS", "The maximum clicks per second limit.", 15, 5, 20);
    private final IntegerValue updateChance = new IntegerValue(this, "Update Chance", "The chance to update the cps.", 80, 0, 100);
    private final IntegerValue failClickChance = new IntegerValue(this, "Fail Click Chance", "The chance to fail a click.", 0, 0, 100);

    private long delay;
    private long lastClickTime;

    public TestClicker(final ClickerModule clickerModule) {
        super(clickerModule, "Test");

        minCPS.onValueChange((oldValue, value) -> {
            if (minCPS.getValue() > maxCPS.getValue()) {
                minCPS.setValue(oldValue);
            }
        });

        maxCPS.onValueChange((oldValue, value) -> {
            if (maxCPS.getValue() < minCPS.getValue()) {
                maxCPS.setValue(oldValue);
            }
        });
    }

    @Override
    public void onActivate() {
        this.calculateCPS();
        this.lastClickTime = System.currentTimeMillis();
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
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

        long currentTime = System.currentTimeMillis();

        while (currentTime - this.lastClickTime >= this.delay) {
            if (ThreadLocalRandom.current().nextDouble() >= this.failClickChance.getValue() * 0.01) {
                this.clickerModule.onClick();
            } else {
                this.clickerModule.onFailClick();
            }

            this.lastClickTime += this.delay;
            currentTime = System.currentTimeMillis();
        }

        // Random chance to recalculate CPS to introduce more variability
        if (ThreadLocalRandom.current().nextDouble() < this.updateChance.getValue() * 0.01) {
            this.calculateCPS();
        }
    }

    private void calculateCPS() {
        final int cps = (int) Arithmetics.interpolate(this.minCPS.getValue() - 1, this.maxCPS.getValue() + 1, ThreadLocalRandom.current().nextDouble());
        this.delay = 1000 / cps;
    }

}
