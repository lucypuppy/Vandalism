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

package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.base.event.player.AttackListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.entity.Entity;

public class WTabModule extends AbstractModule implements AttackListener, TickGameListener {

    private boolean sprintTab;
    private Entity movementTarget;

    public WTabModule() {
        super(
                "W Tab",
                "Automatically tabs w when you are in combat which applies more velocity to your target.",
                Category.COMBAT
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(
                this,
                TickGameEvent.ID,
                AttackSendEvent.ID
        );
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(
                this,
                TickGameEvent.ID,
                AttackSendEvent.ID
        );
    }

    @Override
    public void onAttackSend(final AttackSendEvent event) {
        if (!(Math.random() * 100 < 80) || this.mc.player == null) {
            return;
        }

        if (this.mc.options.forwardKey.isPressed() && this.movementTarget == null) {
            this.mc.options.forwardKey.setPressed(false);
            this.movementTarget = event.target;
        }

        if (!this.sprintTab && (this.mc.player.isSprinting() || this.mc.options.sprintKey.isPressed())) {
            this.mc.options.sprintKey.setPressed(false);
            this.sprintTab = true;
        }
    }

    @Override
    public void onTick() {
        if (this.mc.player == null) {
            return;
        }

        if (sprintTab) {
            this.mc.options.sprintKey.setPressed(true);
            this.sprintTab = false;
        }

        if (this.movementTarget != null && this.mc.player.distanceTo(this.movementTarget) >= 3.1) {
            this.mc.options.forwardKey.setPressed(true);
            this.movementTarget = null;
        }
    }

}
