/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

import de.florianmichael.rclasses.common.StringUtils;
import de.florianmichael.rclasses.pattern.functional.IName;
import de.nekosarekawaii.vandalism.util.click.impl.BoxMuellerClicker;
import de.nekosarekawaii.vandalism.util.click.impl.CooldownClicker;

public enum ClickType implements IName {

    Cooldown(new CooldownClicker()),
    BoxMueller(new BoxMuellerClicker());

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