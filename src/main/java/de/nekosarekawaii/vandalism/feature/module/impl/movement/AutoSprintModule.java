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
        final boolean sneaking = mc.player.input.sneaking;
        final boolean walking = mc.player.isWalking();
        final boolean canStartSprinting = mc.player.canStartSprinting();
        final boolean onGround = mc.player.hasVehicle() ? mc.player.getVehicle().isOnGround() : mc.player.isOnGround();
        final boolean noSneakingAndNoWalking = !sneaking && !walking;
        if ((onGround || mc.player.isSubmergedInWater()) && noSneakingAndNoWalking && canStartSprinting) {
            mc.player.setSprinting(true);
        }
        if ((!mc.player.isTouchingWater() || mc.player.isSubmergedInWater()) && canStartSprinting) {
            mc.player.setSprinting(true);
        }
        if (mc.player.isSprinting()) {
            final boolean noForwardMovementOrNoSprint = !mc.player.input.hasForwardMovement() || !mc.player.canSprint();
            final boolean isColliding = noForwardMovementOrNoSprint || mc.player.horizontalCollision && !mc.player.collidedSoftly || mc.player.isTouchingWater() && !mc.player.isSubmergedInWater();
            if (mc.player.isSwimming()) {
                if (!mc.player.isOnGround() && !mc.player.input.sneaking && noForwardMovementOrNoSprint || !mc.player.isTouchingWater()) {
                    mc.player.setSprinting(false);
                }
            } else if (isColliding) {
                mc.player.setSprinting(false);
            }
        }
    }
}
