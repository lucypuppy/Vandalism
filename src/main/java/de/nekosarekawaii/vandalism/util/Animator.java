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

package de.nekosarekawaii.vandalism.util;

import de.nekosarekawaii.vandalism.util.interfaces.Easing;
import lombok.Getter;

@Getter
public class Animator {

    private float currentProgress;
    private float currentX;

    public void ease(Easing easing, float newX, float animationSpeed) {
        final float difference = newX - this.currentX;

        if (Math.abs(difference) > 0.01f) {
            this.currentProgress = Math.min(this.currentProgress + animationSpeed / 10, 1.0f);
            this.currentX = easing.ease(this.currentProgress, this.currentX, difference, 1.0f);
        } else {
            this.currentX = newX;
            this.currentProgress = 0f;
        }
    }

}
