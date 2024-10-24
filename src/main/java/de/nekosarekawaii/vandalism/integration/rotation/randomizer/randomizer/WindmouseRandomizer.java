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

package de.nekosarekawaii.vandalism.integration.rotation.randomizer.randomizer;

import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.LongValue;
import de.nekosarekawaii.vandalism.integration.rotation.randomizer.Randomizer;
import lombok.Setter;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ThreadLocalRandom;

@Setter
public class WindmouseRandomizer extends Randomizer {

    private final DoubleValue gravityValue = new DoubleValue(this, "Gravity", "The gravity force applied to the vector.", 9.8, 0.1, 20.0);
    private final DoubleValue windValue = new DoubleValue(this, "Wind", "The wind force applied to the vector.", 0.3, 0.1, 20.0);
    private final DoubleValue frictionValue = new DoubleValue(this, "Friction", "The friction force applied to the vector.", 0.98, 0.1, 20.0);
    private final DoubleValue smoothingFactorValue = new DoubleValue(this, "Smoothing Factor", "The smoothing factor applied to the vector.", 0.1, 0.1, 1.0);
    private final DoubleValue oscillationFrequencyValue = new DoubleValue(this, "Oscillation Frequency", "The oscillation frequency applied to the vector.", 0.1, 0.1, 20.0);
    private final DoubleValue elasticityValue = new DoubleValue(this, "Elasticity", "The elasticity applied to the vector.", 0.9, 0.1, 1.0);
    private final DoubleValue updateFrequencyValue = new DoubleValue(this, "Update Frequency", "The update frequency applied to the vector.", 0.1, 0.1, 1.0);
    private final LongValue randomizeDelay = new LongValue(this, "Randomize Delay", "The delay in milliseconds before the vector is randomized.", 0L, 0L, 1000L);

    private Vec3d currentPoint;
    private long lastRandomizeTime;

    public WindmouseRandomizer() {
        super("Windmouse");
    }

    @Override
    public Vec3d randomiseRotationVec3d(final Vec3d vec3d) {
        if (this.currentPoint != null && updateFrequencyValue.getValue() < 1.0 && ThreadLocalRandom.current().nextDouble() > updateFrequencyValue.getValue() || System.currentTimeMillis() - lastRandomizeTime < randomizeDelay.getValue()) {
            return this.currentPoint;
        }

        double x = vec3d.x;
        double y = vec3d.y;
        double z = vec3d.z;

        final double gravity = gravityValue.getValue();
        final double wind = windValue.getValue();
        final double friction = frictionValue.getValue();
        final double smoothingFactor = smoothingFactorValue.getValue();
        final double oscillationFrequency = oscillationFrequencyValue.getValue();
        final double elasticity = elasticityValue.getValue();

        // Dynamic wind variation over time
        final double time = System.currentTimeMillis() * 0.001; // Use current time for dynamic wind
        final double dynamicWind = wind + (ThreadLocalRandom.current().nextGaussian() * 0.05); // Small variation in wind strength

        // Initial wind force
        final double windX = ThreadLocalRandom.current().nextGaussian() * dynamicWind * friction;
        final double windY = ThreadLocalRandom.current().nextGaussian() * dynamicWind * friction;
        final double windZ = ThreadLocalRandom.current().nextGaussian() * dynamicWind * friction;

        // Calculate the effect of gravity on the Y axis
        final double velocityX = (windX + Math.sin(time * oscillationFrequency)) * friction;
        final double velocityZ = (windZ + Math.cos(time * oscillationFrequency)) * friction;
        final double velocityY = windY + gravity * 0.01;

        // Calculate the target positions
        double targetX = x + velocityX;
        double targetY = y + velocityY;
        double targetZ = z + velocityZ;

        // Apply elasticity to create a bouncing effect
        if (Math.abs(targetX - x) > 1.0) {
            targetX = x + (-velocityX * elasticity);
        }
        if (Math.abs(targetY - y) > 1.0) {
            targetY = y + (-velocityY * elasticity);
        }
        if (Math.abs(targetZ - z) > 1.0) {
            targetZ = z + (-velocityZ * elasticity);
        }

        x += (targetX - x) * smoothingFactor;
        y += (targetY - y) * smoothingFactor;
        z += (targetZ - z) * smoothingFactor;

        this.currentPoint = new Vec3d(x, y, z);
        lastRandomizeTime = System.currentTimeMillis();
        return this.currentPoint;
    }

}
