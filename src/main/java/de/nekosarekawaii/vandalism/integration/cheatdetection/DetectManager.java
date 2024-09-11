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

package de.nekosarekawaii.vandalism.integration.cheatdetection;

import de.nekosarekawaii.vandalism.integration.cheatdetection.detectionplayer.DetectionPlayer;
import de.nekosarekawaii.vandalism.integration.cheatdetection.impl.TestDetection;
import de.nekosarekawaii.vandalism.util.NamedStorage;

public class DetectManager extends NamedStorage<AbstractDetection> {

    private final DetectionPlayer player;

    public DetectManager(final DetectionPlayer player) {
        this.player = player;
    }

    @Override
    public void init() {
        this.add(new TestDetection(player));
    }

}
