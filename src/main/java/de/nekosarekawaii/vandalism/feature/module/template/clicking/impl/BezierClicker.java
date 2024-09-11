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

package de.nekosarekawaii.vandalism.feature.module.template.clicking.impl;

import de.nekosarekawaii.vandalism.base.value.impl.number.BezierValue;
import de.nekosarekawaii.vandalism.feature.module.template.clicking.Clicker;
import de.nekosarekawaii.vandalism.util.MSTimer;
import de.nekosarekawaii.vandalism.util.RandomUtils;
import lombok.Setter;

import java.util.concurrent.ThreadLocalRandom;

public class BezierClicker extends Clicker {

    private final MSTimer msTimer = new MSTimer();
    private int delay;
    private float cps;
    private float partialDelays;

    @Setter
    private float cpsUpdatePossibility;

    private BezierValue value;
    private float percentage = 0.0f;
    private int clicks;

    @Override
    public void onUpdate() {
        if (this.value == null) {
            return;
        }

        while (this.clicks > 0) {
            this.clickAction.accept(true);
            this.clicks--;
        }
    }

    @Override
    public void onRotate() {
        if (this.value == null) {
            return;
        }

        this.percentage += ThreadLocalRandom.current().nextFloat() * 0.01f;
        if (this.percentage > 1.0f) this.percentage = 0.0f;

        if (this.msTimer.hasReached(this.delay, true)) {
            this.clickAction.accept(false);

            if (RandomUtils.randomInt(0, 100) <= this.cpsUpdatePossibility || this.cps < 3) {
                this.cps = this.value.getValue(this.percentage);
            }

            final float delay = 1000.0f / this.cps;
            this.delay = (int) Math.floor(delay + this.partialDelays);
            this.partialDelays += delay - this.delay;

            this.clicks++;
        }
    }

    public void setBezierValue(final BezierValue value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return "Bezier";
    }

}