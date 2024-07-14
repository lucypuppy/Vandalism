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

package de.nekosarekawaii.vandalism.integration.rotation.randomizer.randomizer;

import de.nekosarekawaii.vandalism.integration.rotation.randomizer.Randomizer;
import net.minecraft.util.math.Vec3d;

public class NoneRandomizer extends Randomizer {

    @Override
    public Vec3d randomiseRotationVec3d(Vec3d vec3d) {
        return vec3d;
    }

    @Override
    public String getName() {
        return "None";
    }

}
