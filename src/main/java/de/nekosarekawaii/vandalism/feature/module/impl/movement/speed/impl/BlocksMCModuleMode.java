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
import de.nekosarekawaii.vandalism.event.game.TickTimeListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.speed.SpeedModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.player.MovementUtil;

public class BlocksMCModuleMode extends ModuleMulti<SpeedModule> implements PlayerUpdateListener, TickTimeListener {

    private float charge = 0;
    private int fallState = 0;
    private boolean lastJumpPressed = false;

    public BlocksMCModuleMode() {
        super("BlocksMC");
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
        if (!MovementUtil.isMoving()) {
            this.charge = 0.0f;
            return;
        }

        this.lastJumpPressed = this.mc.options.jumpKey.isPressed();
        this.mc.options.jumpKey.setPressed(false);

        if (this.mc.player.isOnGround()) {
            this.mc.player.jump();
            MovementUtil.setSpeed(0.45);

            this.charge = 3.0f;
        } else {
            if (mc.player.fallDistance > 0.0f) {
                this.fallState++;
                this.charge += 1.0f;
            } else {
                this.fallState = 0;
                this.charge = 0.0f;
            }

            if (this.fallState == 1) {
                MovementUtil.setSpeed(0.34);
            } else if (this.fallState == 2) {
                MovementUtil.setSpeed(0.25);
            } else {
                MovementUtil.setSpeed(MovementUtil.getSpeed());
            }
        }
    }

    @Override
    public void onPostPlayerUpdate(final PlayerUpdateEvent event) {
        this.mc.options.jumpKey.setPressed(this.lastJumpPressed);
    }

    @Override
    public void onTickTimings(TickTimeEvent e) {
        if (MovementUtil.isMoving() && this.charge > 0.0f) {
            e.tickTime = 1000f / (20.0f + this.charge);
        }
    }

}
