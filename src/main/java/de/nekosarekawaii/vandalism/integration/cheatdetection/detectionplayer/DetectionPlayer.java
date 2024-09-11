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

package de.nekosarekawaii.vandalism.integration.cheatdetection.detectionplayer;

import de.nekosarekawaii.vandalism.integration.cheatdetection.DetectManager;
import de.nekosarekawaii.vandalism.integration.cheatdetection.Detection;
import de.nekosarekawaii.vandalism.integration.cheatdetection.detectionplayer.impl.RotationData;
import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;

@Getter
public class DetectionPlayer {

    private final PlayerEntity player;
    private final DetectManager detectManager;
    private final RotationData rotationData;

    public DetectionPlayer(PlayerEntity player) {
        this.player = player;

        this.detectManager = new DetectManager(this);
        this.detectManager.init();

        this.rotationData = new RotationData(this);
    }

    public void onActivate() {
        this.rotationData.onActivate();
        this.detectManager.getList().forEach(Detection::onActivate);
    }

    public void onDeactivate() {
        this.rotationData.onDeactivate();
        this.detectManager.getList().forEach(Detection::onDeactivate);
    }

}
