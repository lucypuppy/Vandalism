/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.integration;

import de.nekosarekawaii.vandalism.util.EvictingList;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ClickPredictor {

    private final EvictingList<Long> clickIntervals = new EvictingList<>(new ArrayList<>(), 200);

    private long lastClick = -1;

    @Getter
    private double estimatedDelta = 0;
    private double errorCovariance = 1;

    private void predictNextDelta() {
        final List<Long> clickIntervals = this.clickIntervals.getNormalList();
        final int size = clickIntervals.size();

        if (size < 20) {
            return;
        }

        double weightedSum = 0;
        double totalWeight = 0;
        int index = 1;

        final double weightFactor = 0.9;
        for (final long interval : clickIntervals) {
            final double weight = Math.pow(weightFactor, clickIntervals.size() - index++);

            weightedSum += interval * weight;
            totalWeight += weight;
        }

        final double movingAveragePrediction = weightedSum / totalWeight;
        final double measurementNoise = 1e-1;
        final double processNoise = 1e-5;

        final double kalmanGain = this.errorCovariance / (this.errorCovariance + measurementNoise);
        this.estimatedDelta += kalmanGain * (movingAveragePrediction - this.estimatedDelta);
        this.errorCovariance = (1 - kalmanGain) * this.errorCovariance + processNoise;
    }

    public void click() {
        final long current = System.currentTimeMillis();
        final long delta = current - this.lastClick;

        if (this.lastClick <= 0 || delta > 1000) {
            this.lastClick = current;
            return;
        }

        predictNextDelta();
        this.clickIntervals.add(delta);
        this.lastClick = current;
    }

}