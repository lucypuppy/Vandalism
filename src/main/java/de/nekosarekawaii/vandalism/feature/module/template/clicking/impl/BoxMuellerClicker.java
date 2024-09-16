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

import de.nekosarekawaii.vandalism.feature.module.template.clicking.Clicker;
import de.nekosarekawaii.vandalism.util.*;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector4d;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class BoxMuellerClicker extends Clicker {

    private int delay;

    @Setter
    private float mean;

    @Setter
    private float std;
    private float cps;

    @Getter
    @Setter
    private int clicks;

    @Setter
    private int minCps;

    @Setter
    private int maxCps;

    private final MSTimer msTimer = new MSTimer();
    private float partialDelays;

    @Setter
    private float cpsUpdatePossibility;

    @Getter
    private final EvictingList<Vector4d> cpsHistory = new EvictingList<>(new ArrayList<>(), 200);

    @Override
    public void onUpdate() {
        while (this.clicks + RandomUtils.randomIndex(-1, 1) > 0) {
            this.clickAction.accept(true);
            this.clicks--;
        }
    }

    @Override
    public void onRotate() {
        if (this.msTimer.hasReached(this.delay, true)) {
            this.clickAction.accept(false);
            if (RandomUtils.randomInt(0, 100) <= this.cpsUpdatePossibility || this.cps < this.minCps) {
                int depth = 0;
                while (true) {
                    final double gaussian = ThreadLocalRandom.current().nextGaussian(this.mean, this.std);
                    final double gaussianPercentage = MathUtil.normalizeGaussian(gaussian, this.mean, this.std);
                    final double gaussianDensity = MathUtil.densityFunction(gaussian, this.mean, this.std) * this.mean;
                    depth++;
                    if (depth > 5 || gaussianPercentage < gaussianDensity) {
                        this.cps = (float) Arithmetics.interpolate(this.minCps, this.maxCps, gaussianPercentage);
                        this.cpsHistory.add(new Vector4d(this.cps, gaussian, gaussianPercentage, gaussianDensity));
                        break;
                    }
                }
            }
            final float delay = 1000.0f / this.cps;
            this.delay = (int) Math.floor(delay + this.partialDelays);
            this.partialDelays += delay - this.delay;
            this.clicks++;
        }
    }

    @Override
    public String getName() {
        return "Box Mueller";
    }

}