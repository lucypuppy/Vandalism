/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.feature.module.template;

import de.florianmichael.rclasses.math.timer.MSTimer;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;

public abstract class MultiExecutionModuleMode<M extends AbstractModule> extends SingleExecutionModuleMode<M> implements PlayerUpdateListener {

    private final IntegerValue delay = new IntegerValue(
            this,
            "Delay",
            "The delay between executions.",
            0,
            0,
            10000
    );

    private final MSTimer delayTimer = new MSTimer();

    public MultiExecutionModuleMode(final String name, final M parent) {
        super(name, parent);
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
        if (this.delayTimer.hasReached(this.delay.getValue(), true)) {
            for (int i = 0; i < this.times.getValue(); i++) {
                if (this.mc.player == null) {
                    continue;
                }
                this.onExecute();
            }
        }
    }

}
