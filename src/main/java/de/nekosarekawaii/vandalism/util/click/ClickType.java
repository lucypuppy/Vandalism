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

package de.nekosarekawaii.vandalism.util.click;

import de.nekosarekawaii.vandalism.util.click.impl.BezierClicker;
import de.nekosarekawaii.vandalism.util.click.impl.BoxMuellerClicker;
import de.nekosarekawaii.vandalism.util.click.impl.CooldownClicker;
import de.nekosarekawaii.vandalism.util.common.IName;
import de.nekosarekawaii.vandalism.util.common.StringUtils;

public enum ClickType implements IName {

    COOLDOWN(new CooldownClicker()),
    BOXMUELLER(new BoxMuellerClicker()),
    BEZIER(new BezierClicker());

    private final String name;
    private final Clicker clicker;

    ClickType(final Clicker clicker) {
        this.name = StringUtils.normalizeEnumName(this.name());
        this.clicker = clicker;
    }

    public Clicker getClicker() {
        return clicker;
    }

    @Override
    public String getName() {
        return name;
    }

}