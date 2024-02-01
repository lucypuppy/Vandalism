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
import de.nekosarekawaii.vandalism.base.event.normal.player.AttackListener;
import de.nekosarekawaii.vandalism.base.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.entity.Entity;

public class SprintTapModule extends AbstractModule implements AttackListener, PlayerUpdateListener {

    private boolean sprintTap = false;
    private Entity movementTarget = null;

    public SprintTapModule() {
        super(
                "Sprint Tap",
                "Automatically sprints and un-sprints when you are in combat which applies more velocity to your target.",
                Category.COMBAT
        );
    }

    private void reset() {
        this.sprintTap = false;
        this.movementTarget = null;
    }

    @Override
    public void onActivate() {
        this.reset();
        Vandalism.getInstance().getEventSystem().subscribe(
                this,
                PlayerUpdateEvent.ID,
                AttackSendEvent.ID
        );
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(
                this,
                PlayerUpdateEvent.ID,
                AttackSendEvent.ID
        );
        this.reset();
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
        if (!this.sprintTap && (this.mc.player.isSprinting() || this.mc.options.sprintKey.isPressed())) {
            this.mc.options.sprintKey.setPressed(false);
            this.sprintTap = true;
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.sprintTap) {
            this.mc.options.sprintKey.setPressed(true);
            this.sprintTap = false;
        }
        if (this.movementTarget != null && this.mc.player.distanceTo(this.movementTarget) >= 3.1) {
            this.mc.options.forwardKey.setPressed(true);
            this.movementTarget = null;
        }
    }

}
