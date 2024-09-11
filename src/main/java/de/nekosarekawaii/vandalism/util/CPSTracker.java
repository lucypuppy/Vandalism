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

import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;

public class CPSTracker {

    private final List<Long> clicks = new ArrayList<>();

    public void update() {
        if (this.clicks.isEmpty()) return;
        this.clicks.removeIf((click) -> Util.getMeasuringTimeMs() - click > 1000);
    }

    public void click() {
        this.clicks.add(Util.getMeasuringTimeMs());
    }

    public int clicks() {
        return this.clicks.size();
    }

}
