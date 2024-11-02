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

package de.nekosarekawaii.vandalism.integration.rotation.randomizer;

import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.impl.selection.ClassModeValue;
import de.nekosarekawaii.vandalism.integration.rotation.randomizer.randomizer.NoneRandomizer;
import de.nekosarekawaii.vandalism.integration.rotation.randomizer.randomizer.SimplexRandomizer;
import de.nekosarekawaii.vandalism.integration.rotation.randomizer.randomizer.WindmouseRandomizer;

public class RandomizerModeValue extends ClassModeValue<Randomizer> {

    public RandomizerModeValue(ValueParent parent, String name, String description) {
        super(parent, name, description, new NoneRandomizer(), new SimplexRandomizer(), new WindmouseRandomizer());

        for (final Randomizer randomizer : getOptions()) {
            for (final Value<?> value : randomizer.getValues()) {
                value.visibleCondition(() -> this.getValue() == randomizer);
                parent.getValues().add(value);
            }
        }
    }

}
