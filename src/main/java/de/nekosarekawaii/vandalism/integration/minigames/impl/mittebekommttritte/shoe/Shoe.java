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

package de.nekosarekawaii.vandalism.integration.minigames.impl.mittebekommttritte.shoe;

public class Shoe {

    private final String name;
    private final int price;
    private boolean unlocked;

    public Shoe(final String name, final int price) {
        this(name, price, false);
    }

    protected Shoe(final String name, final int price, final boolean unlocked) {
        this.name = name;
        this.price = price;
        this.unlocked = unlocked;
    }

    public String getName() {
        return this.name;
    }

    public int getPrice() {
        return this.price;
    }

    public boolean isUnlocked() {
        return this.unlocked;
    }

    public void setUnlocked(final boolean unlocked) {
        this.unlocked = unlocked;
    }

}
