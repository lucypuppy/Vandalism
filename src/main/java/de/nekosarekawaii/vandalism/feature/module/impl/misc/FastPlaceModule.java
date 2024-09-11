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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.feature.module.Module;

public class FastPlaceModule extends Module {

    public final IntegerValue cooldown = new IntegerValue(
            this,
            "Cooldown",
            "Here you can input the cooldown value.",
            0,
            0,
            3
    );

    public FastPlaceModule() {
        super("Fast Place", "Allows you to place blocks faster and to throw throwable items faster.", Category.MISC);
    }

}
