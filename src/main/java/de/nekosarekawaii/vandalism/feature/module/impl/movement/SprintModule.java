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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.florianmichael.dietrichevents2.Priorities;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;

public class SprintModule extends AbstractModule implements PlayerUpdateListener {

    private final BooleanValue legit = new BooleanValue(
            this,
            "Legit",
            "Only sprints if its allowed.",
            true
    );

    public SprintModule() {
        super("Sprint", "Automatically let's you sprint!", Category.MOVEMENT);
    }

    @Override
    public void onActivate() { // Low Priority so other modules can easly override this.
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this, Priorities.LOW);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);

        if (this.mc.player != null)
            this.mc.player.setSprinting(mc.options.sprintKey.isPressed());
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        mc.options.sprintKey.setPressed(true);

        if (legit.getValue()) {
            return;
        }

        if (!mc.player.horizontalCollision
                && !mc.player.isSneaking()
                && !mc.player.isSprinting()
                && (Math.abs(mc.player.input.movementForward) >= 0.8F || Math.abs(mc.player.sidewaysSpeed) >= 0.8F)) {
            this.mc.player.setSprinting(true);
        }
    }

}
