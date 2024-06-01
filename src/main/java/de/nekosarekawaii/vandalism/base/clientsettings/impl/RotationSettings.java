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

package de.nekosarekawaii.vandalism.base.clientsettings.impl;

import de.nekosarekawaii.vandalism.base.clientsettings.ClientSettings;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.ModeValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.integration.newrotation.enums.RotationGCD;

public class RotationSettings extends ValueGroup {

    public final FloatValue rotateSpeed = new FloatValue(
            this,
            "Rotate Back Rotate Speed",
            "The speed of the rotation.",
            60.0f,
            0.0f,
            180.0f
    );

    public final FloatValue correlationStrength = new FloatValue(
            this,
            "Rotate Back Correlation Strength",
            "The strength of the correlation.",
            0.2f,
            0.0f,
            1.0f
    );

    public final EnumModeValue<RotationGCD> gcdMode = new EnumModeValue<>(
            this,
            "GCD Mode",
            "Change the GCD Mode.",
            RotationGCD.REAL,
            RotationGCD.values()
    );

    public final ModeValue moveFixMode = new ModeValue(
            this,
            "Movement Fix Mode",
            "Change the behaviour of the movement fix.",
            "Silent", "Strict"
    );

    public RotationSettings(final ClientSettings parent) {
        super(parent, "Rotation", "Rotation related settings.");
    }

}
