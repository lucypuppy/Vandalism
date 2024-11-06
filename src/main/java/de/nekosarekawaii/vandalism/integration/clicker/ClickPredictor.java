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

package de.nekosarekawaii.vandalism.integration.clicker;

import de.nekosarekawaii.vandalism.util.EvictingList;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ClickPredictor {

    private final EvictingList<Long> clickIntervals = new EvictingList<>(new ArrayList<>(), 200);

    @Getter
    private double estimatedDelta = 0;
    private double errorCovariance = 1;

    private void predictNextDelta() {
        final List<Long> clickIntervals = this.clickIntervals.getNormalList();
        final int size = clickIntervals.size();

        if (size < 20) {
            return;
        }

        // Parameters for adaptive weighting and dynamic smoothing
        final double baseWeightFactor = 0.9;
        final double minWeightFactor = 0.85;
        final double varianceThreshold = 5.0; // Threshold to adjust weight factor
        final double shortIntervalThreshold = 100; // Define what qualifies as a "short" interval
        final double highSmoothingFactor = 0.15;
        final double lowSmoothingFactor = 0.05;
        final double tolerance = 0.01; // Threshold for early return

        // Calculate recent variance to adjust weight factor
        double recentVariance = 0.0;
        for (int i = size - 5; i < size; i++) {  // Last 5 intervals for variance
            recentVariance += Math.pow(clickIntervals.get(i) - this.estimatedDelta, 2);
        }
        recentVariance = Math.sqrt(recentVariance / 5);

        // Adaptive weight factor
        double adaptiveWeightFactor = baseWeightFactor - (recentVariance / varianceThreshold);
        adaptiveWeightFactor = Math.max(adaptiveWeightFactor, minWeightFactor); // Ensure minimum

        // Calculate weighted moving average
        double weightedSum = 0;
        double totalWeight = 0;
        double currentWeight = 1.0;
        for (int i = size - 1; i >= 0; i--) {  // Reverse loop for recent intervals
            final long interval = clickIntervals.get(i);
            weightedSum += interval * currentWeight;
            totalWeight += currentWeight;
            currentWeight *= adaptiveWeightFactor;
        }

        final double movingAveragePrediction = weightedSum / totalWeight;

        // Dynamic smoothing factor based on interval length
        final double dynamicSmoothingFactor = (movingAveragePrediction < shortIntervalThreshold) ? highSmoothingFactor : lowSmoothingFactor;

        // Apply exponential smoothing
        final double updatedDelta = dynamicSmoothingFactor * movingAveragePrediction + (1 - dynamicSmoothingFactor) * this.estimatedDelta;

        // Early return if change is below tolerance threshold to save computation
        if (Math.abs(updatedDelta - this.estimatedDelta) < tolerance) {
            return;
        }

        this.estimatedDelta = updatedDelta;

        // Optional: Update error covariance with gradual decay if intervals are stable
        if (size % 5 == 0) {
            final double measurementNoise = 0.1;
            final double processNoise = 1e-5;

            final double kalmanGain = this.errorCovariance / (this.errorCovariance + measurementNoise);
            this.estimatedDelta += kalmanGain * (movingAveragePrediction - this.estimatedDelta);
            this.errorCovariance = (1 - kalmanGain) * this.errorCovariance + processNoise;

            // Apply decay to error covariance during stable periods
            if (Math.abs(movingAveragePrediction - this.estimatedDelta) < tolerance) {
                this.errorCovariance *= 0.99; // Gradual decay for drift prevention
            }
        }
    }

    public void click(final long delta) {
        if (delta > 1000) {
            return;
        }

        predictNextDelta();
        this.clickIntervals.add(delta);
    }

    public double getPredictedCPS() {
        if (this.estimatedDelta == 0) {
            return 0.0;
        }

        return 1000.0 / this.estimatedDelta;
    }

}