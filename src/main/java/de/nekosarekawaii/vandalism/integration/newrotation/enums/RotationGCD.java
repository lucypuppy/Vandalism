/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.integration.newrotation.enums;

import com.mojang.datafixers.util.Function4;
import de.nekosarekawaii.vandalism.integration.newrotation.Rotation;
import de.nekosarekawaii.vandalism.util.common.IName;
import de.nekosarekawaii.vandalism.util.common.StringUtils;
import net.minecraft.util.math.MathHelper;

public enum RotationGCD implements IName {

    NONE((rotation, lastRotation, multiplier, iterations) -> rotation),
    REAL((rotation, lastRotation, multiplier, iterations) -> {
        float yaw = rotation.getYaw(), pitch = rotation.getPitch(),
                lastYaw = lastRotation.getYaw(), lastPitch = lastRotation.getPitch(),
                deltaYaw = MathHelper.subtractAngles(lastYaw, yaw),
                deltaPitch = MathHelper.subtractAngles(lastPitch, pitch);

        yaw = lastYaw;
        pitch = lastPitch;

        final float gcdCursorDeltaX = deltaYaw / 0.15f,
                gcdCursorDeltaY = deltaPitch / 0.15f;

        final double cursorDeltaX = gcdCursorDeltaX / multiplier,
                cursorDeltaY = gcdCursorDeltaY / multiplier;

        double partialDeltaX = 0, partialDeltaY = 0;

        for (int i = 0; i < iterations; i++) {
            final double nextDeltaX = cursorDeltaX / iterations,
                    nextDeltaY = cursorDeltaY / iterations;

            final int currentDeltaX = (int) Math.round(nextDeltaX + partialDeltaX),
                    currentDeltaY = (int) Math.round(nextDeltaY + partialDeltaY);

            partialDeltaX += nextDeltaX - currentDeltaX;
            partialDeltaY += nextDeltaY - currentDeltaY;

            final double newCursorDeltaX = currentDeltaX * multiplier,
                    newCursorDeltaY = currentDeltaY * multiplier;

            yaw += (float) newCursorDeltaX * 0.15f;
            pitch += (float) newCursorDeltaY * 0.15f;
        }

        return new Rotation(yaw, pitch);
    });

    private final String name;
    private final Function4<Rotation, Rotation, Double, Integer, Rotation> lambda;

    RotationGCD(final Function4<Rotation, Rotation, Double, Integer, Rotation> lambda) {
        this.name = StringUtils.normalizeEnumName(this.name());
        this.lambda = lambda;
    }

    public Function4<Rotation, Rotation, Double, Integer, Rotation> getLambda() {
        return lambda;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
