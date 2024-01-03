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

package de.nekosarekawaii.vandalism.integration.clicker.impl;

import de.florianmichael.rclasses.common.RandomUtils;
import de.florianmichael.rclasses.math.BoxMullerTransform;
import de.florianmichael.rclasses.math.integration.MSTimer;
import de.florianmichael.rclasses.pattern.evicting.EvictingList;
import de.nekosarekawaii.vandalism.integration.clicker.Clicker;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class BoxMuellerClicker extends Clicker {

    private int delay;
    private float mean;
    private float std;
    private float cps;
    private final MSTimer msTimer = new MSTimer();
    private float partialDelays;
    private float cpsUpdatePossibility;
    private final EvictingList<Pair<Integer, Integer>> delayHistory = new EvictingList<>(new ArrayList<>(), 100);

    @Override
    public void onUpdate() {
        if (!this.msTimer.hasReached(this.delay, true)) {
            this.clickAction.accept(false);
            return;
        }

        final ThreadLocalRandom random = ThreadLocalRandom.current();
        if (RandomUtils.randomInt(0, 100) <= this.cpsUpdatePossibility || this.cps < 3) {
            this.cps = BoxMullerTransform.distribution(random, 1, 20, this.mean, this.std);
        }

        final float delay = 1000.0f / this.cps;
        this.delay = (int) Math.floor(delay + this.partialDelays);
        this.partialDelays += delay - this.delay;

        this.delayHistory.add(new Pair<>(this.delay, (int) this.cps));
        this.clickAction.accept(true);
    }

    public void setMean(final float mean) {
        this.mean = mean;
    }

    public void setStd(final float std) {
        this.std = std;
    }

    public void setCpsUpdatePossibility(final float possibility) {
        this.cpsUpdatePossibility = possibility;
    }

    public EvictingList<Pair<Integer, Integer>> getDelayHistory() {
        return delayHistory;
    }

}