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

package de.nekosarekawaii.vandalism.integration;

import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import lombok.Getter;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CPSTracker implements MinecraftWrapper {

    private final List<Long> leftClicks;
    private final List<Long> rightClicks;
    private final ClickPredictor clickPredictor;

    public CPSTracker() {
        this.leftClicks = new ArrayList<>();
        this.rightClicks = new ArrayList<>();
        this.clickPredictor = new ClickPredictor();
    }

    public void update() {
        if (!this.leftClicks.isEmpty()) {
            this.leftClicks.removeIf((click) -> Util.getMeasuringTimeMs() - click > 1000);
        }
        if (!this.rightClicks.isEmpty()) {
            this.rightClicks.removeIf((click) -> Util.getMeasuringTimeMs() - click > 1000);
        }
    }

    public void leftClick() {
        this.leftClicks.add(Util.getMeasuringTimeMs());
        this.clickPredictor.click();
    }

    public void rightClick() {
        this.rightClicks.add(Util.getMeasuringTimeMs());
        this.clickPredictor.click();
    }

    public int getLeftClicks() {
        return this.leftClicks.size();
    }

    public int getRightClicks() {
        return this.rightClicks.size();
    }

}
