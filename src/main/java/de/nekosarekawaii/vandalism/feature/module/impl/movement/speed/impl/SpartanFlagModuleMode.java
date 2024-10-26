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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.speed.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.speed.SpeedModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.player.MovementUtil;

public class SpartanFlagModuleMode extends ModuleMulti<SpeedModule> implements PlayerUpdateListener {

    private double lastRandom;

    private final DoubleValue speed = new DoubleValue(
            this,
            "Speed",
            "The speed amount of the long jump speed.",
            1.2,
            1.0,
            5.0
    );

    public SpartanFlagModuleMode() {
        super("Spartan Flag");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (MovementUtil.isMoving() && !mc.player.isInFluid()) {
            if (this.mc.player.isOnGround()) {
                this.mc.player.jump();
            }

            double random = Math.random() * 2.0;
            if (Math.abs(this.lastRandom - random) < 0.5) {
                random *= Math.random();
            }
            this.lastRandom = random;

            MovementUtil.setSpeed(this.speed.getValue() + random);
        }
    }

}
