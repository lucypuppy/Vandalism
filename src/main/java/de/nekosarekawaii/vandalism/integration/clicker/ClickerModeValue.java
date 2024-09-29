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

package de.nekosarekawaii.vandalism.integration.clicker;

import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.impl.selection.ClassModeValue;
import de.nekosarekawaii.vandalism.feature.module.template.module.ClickerModule;
import de.nekosarekawaii.vandalism.integration.clicker.clickers.CooldownClicker;
import de.nekosarekawaii.vandalism.integration.clicker.clickers.GaussianClicker;
import de.nekosarekawaii.vandalism.integration.clicker.clickers.TestClicker;

public class ClickerModeValue extends ClassModeValue<Clicker> {

    public ClickerModeValue(final ValueParent parent, final String name, final String description, final ClickerModule clickerModule) {
        super(parent, name, description, new CooldownClicker(clickerModule), new GaussianClicker(clickerModule), new TestClicker(clickerModule));

        for (final Clicker clicker : getOptions()) {
            for (final Value<?> value : clicker.getValues()) {
                value.visibleCondition(() -> this.getValue() == clicker);
                parent.getValues().add(value);
            }
        }
    }

    public boolean isSelected(final Clicker clicker) {
        return this.getValue() == clicker;
    }

}
