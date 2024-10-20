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
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;

public class AutoSprintModule extends Module implements PlayerUpdateListener {

    public AutoSprintModule() {
        super(
                "Auto Sprint",
                "Automatically lets you sprint!",
                Category.MOVEMENT
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this, Priorities.LOW);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        final boolean sneaking = this.mc.player.input.sneaking;
        final boolean walking = this.mc.player.isWalking();
        final boolean canStartSprinting = this.mc.player.canStartSprinting();
        final boolean onGround = this.mc.player.hasVehicle() ? this.mc.player.getVehicle().isOnGround() : this.mc.player.isOnGround();
        final boolean noSneakingAndNoWalking = !sneaking && !walking;
        if ((onGround || this.mc.player.isSubmergedInWater()) && noSneakingAndNoWalking && canStartSprinting) {
            this.mc.player.setSprinting(true);
        }
        if ((!this.mc.player.isTouchingWater() || this.mc.player.isSubmergedInWater()) && canStartSprinting) {
            this.mc.player.setSprinting(true);
        }
        if (this.mc.player.isSprinting()) {
            final boolean noForwardMovementOrNoSprint = !this.mc.player.input.hasForwardMovement() || !this.mc.player.canSprint();
            final boolean isColliding = noForwardMovementOrNoSprint || this.mc.player.horizontalCollision && !this.mc.player.collidedSoftly || this.mc.player.isTouchingWater() && !this.mc.player.isSubmergedInWater();
            if (this.mc.player.isSwimming()) {
                if (!this.mc.player.isOnGround() && !this.mc.player.input.sneaking && noForwardMovementOrNoSprint || !this.mc.player.isTouchingWater()) {
                    this.mc.player.setSprinting(false);
                }
            } else if (isColliding) {
                this.mc.player.setSprinting(false);
            }
        }
    }
}
