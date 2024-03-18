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

package de.nekosarekawaii.vandalism.base.clientsettings.impl;

import de.nekosarekawaii.vandalism.base.clientsettings.ClientSettings;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;

public class MovementSettings extends ValueGroup {

    public final BooleanValue customizeRiptideBoostMultiplier = new BooleanValue(
            this,
            "Customize Riptide Boost Multiplier",
            "If activated shows you a slider to modify the riptide boost multiplier.",
            false
    );

    public final FloatValue riptideBoostMultiplier = new FloatValue(
            this,
            "Riptide Boost Multiplier",
            "Lets you modify the riptide boost multiplier.",
            1.0f,
            -5.0f,
            5.0f
    ).visibleCondition(this.customizeRiptideBoostMultiplier::getValue);

    public MovementSettings(final ClientSettings parent) {
        super(parent, "Movement", "Movement related settings.");
    }

}
