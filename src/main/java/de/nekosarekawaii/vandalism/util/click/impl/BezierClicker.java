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

package de.nekosarekawaii.vandalism.util.click.impl;

import de.florianmichael.rclasses.common.RandomUtils;
import de.florianmichael.rclasses.math.timer.MSTimer;
import de.nekosarekawaii.vandalism.base.value.impl.number.BezierValue;
import de.nekosarekawaii.vandalism.util.click.Clicker;

import java.util.concurrent.ThreadLocalRandom;

public class BezierClicker extends Clicker {

    private final MSTimer msTimer = new MSTimer();
    private int delay;
    private float cps;
    private float partialDelays;
    private float cpsUpdatePossibility;
    private BezierValue value;

    @Override
    public void onUpdate() {
        if (this.value == null || !this.msTimer.hasReached(this.delay, true)) {
            this.clickAction.accept(false);
            return;
        }

        if (RandomUtils.randomInt(0, 100) <= this.cpsUpdatePossibility || this.cps < 3) {
            this.cps = this.value.getValue(ThreadLocalRandom.current().nextFloat());
        }

        final float delay = 1000.0f / this.cps;
        this.delay = (int) Math.floor(delay + this.partialDelays);
        this.partialDelays += delay - this.delay;

        this.clickAction.accept(true);
    }

    public void setBezierValue(final BezierValue value) {
        this.value = value;
    }

    public void setCpsUpdatePossibility(final float possibility) {
        this.cpsUpdatePossibility = possibility;
    }

}