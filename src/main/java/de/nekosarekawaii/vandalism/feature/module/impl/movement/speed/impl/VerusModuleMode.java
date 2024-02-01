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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.speed.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.normal.game.TickTimeListener;
import de.nekosarekawaii.vandalism.base.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.speed.SpeedModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;
import de.nekosarekawaii.vandalism.util.game.MovementUtil;

public class VerusModuleMode extends ModuleMulti<SpeedModule> implements PlayerUpdateListener, TickTimeListener {

    private float charge = 0;
    private boolean tick = false;

    public VerusModuleMode() {
        super("Verus");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, TickTimeEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, TickTimeEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.mc.player.isOnGround()) {
            if (MovementUtil.isMoving()) {
                this.mc.player.jump();
                MovementUtil.setSpeed(0.45);

                tick = false;
            }

            charge = 3.0f;
        } else {
            charge = 0.0f;
        }
    }

    @Override
    public void onPostPlayerUpdate(final PlayerUpdateEvent event) {
        if (mc.player.fallDistance > 0.0f && !tick) {
            MovementUtil.setSpeed(0.3);
            tick = true;
        }
    }

    @Override
    public void onTickTimings(TickTimeEvent e) {
        if (MovementUtil.isMoving()) {
            e.tickTime = 1000f / (20.0f + charge);
        }
    }

}
