/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

package de.nekosarekawaii.vandalism.util.math;

import net.minecraft.client.input.Input;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class MCMathUtil {

    public static List<Input> possibleInputs() {
        final List<Input> inputs = new ArrayList<>();
        for (float forward = -1; forward <= 1; forward++) {
            for (float sideways = -1; sideways <= 1; sideways++) {
                inputs.add(withPressingStates(forward, sideways));
            }
        }
        return inputs;
    }

    public static Input withPressingStates(final float movementForward, final float movementSideways) {
        final Input input = new Input();
        input.movementSideways = movementSideways;
        input.movementForward = movementForward;

        input.pressingForward = movementForward > 0.0F;
        input.pressingBack = movementForward < 0.0F;

        input.pressingLeft = movementSideways > 0.0F;
        input.pressingRight = movementSideways < 0.0F;

        return input;
    }

    public static Vec3d toVec3D(final Vec2f vector, final boolean flipped) {
        return new Vec3d(flipped ? vector.y : vector.x, 0, flipped ? vector.x : vector.y);
    }

}
