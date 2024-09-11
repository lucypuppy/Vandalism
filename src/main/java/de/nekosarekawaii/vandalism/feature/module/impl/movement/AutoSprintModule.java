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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.florianmichael.dietrichevents2.Priorities;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;

public class AutoSprintModule extends Module implements PlayerUpdateListener {

    private final BooleanValue legit = new BooleanValue(
            this,
            "Legit",
            "Only sprints if its allowed.",
            true
    );

    private boolean stopSprinting;

    public AutoSprintModule() {
        super("Auto Sprint", "Automatically lets you sprint!", Category.MOVEMENT);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this, Priorities.LOW);
        stopSprinting = false;
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (stopSprinting) {
            this.mc.options.sprintKey.setPressed(false);
            this.mc.player.setSprinting(false);
            stopSprinting(false);
            return;
        }
        if (this.legit.getValue()) {
            this.mc.options.sprintKey.setPressed(true);
            return;
        }
        if (
                !this.mc.player.horizontalCollision &&
                !this.mc.player.isSneaking() &&
                !this.mc.player.isSprinting() &&
                (Math.abs(this.mc.player.input.movementForward) >= 0.8F || Math.abs(this.mc.player.sidewaysSpeed) >= 0.8F)
        ) {
            this.mc.player.setSprinting(true);
        }
    }

    public void stopSprinting(boolean shouldStop) {
        stopSprinting = shouldStop;
    }

}
