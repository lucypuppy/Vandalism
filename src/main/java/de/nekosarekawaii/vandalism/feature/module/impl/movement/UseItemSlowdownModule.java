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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.normal.player.PlayerSlowdownListener;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.feature.module.impl.combat.AutoBlockModule;

public class UseItemSlowdownModule extends AbstractModule implements PlayerSlowdownListener {

    private final FloatValue forwardMultiplier = new FloatValue(this, "Forward", "", 1.0f, 0.2f, 1.0f);
    private final FloatValue sidewaysMultiplier = new FloatValue(this, "Sideways", "", 1.0f, 0.2f, 1.0f);

    private final AutoBlockModule autoBlockModule;

    public UseItemSlowdownModule(final AutoBlockModule autoBlockModule) {
        super("Use Item Slowdown", "Replaces the slowdown for using items", Category.MOVEMENT);
        this.autoBlockModule = autoBlockModule;
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(PlayerSlowdownEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerSlowdownEvent.ID, this);
    }


    @Override
    public void onSlowdown(PlayerSlowdownEvent event) {
        if (!mc.player.isSneaking() && isBlocking()) {
            event.movementForward = this.forwardMultiplier.getValue();
            event.movementSideways = this.sidewaysMultiplier.getValue();
        }
    }

    private boolean isBlocking() {
        return (this.autoBlockModule.isBlocking() && this.autoBlockModule.isActive()) || mc.player.isUsingItem() || mc.options.useKey.isPressed();
    }

}
