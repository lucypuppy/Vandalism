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
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;

public class AutoSprintModule extends Module implements PlayerUpdateListener {

    public AutoSprintModule() {
        super("Auto Sprint", "Automatically lets you sprint!", Category.MOVEMENT);
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
        boolean bl2 = this.mc.player.input.sneaking;
        boolean bl3 = this.isWalking();
        boolean bl5 = this.canStartSprinting();
        boolean bl6 = this.mc.player.hasVehicle() ? this.mc.player.getVehicle().isOnGround() : this.mc.player.isOnGround();
        boolean bl7 = !bl2 && !bl3;
        if ((bl6 || this.mc.player.isSubmergedInWater()) && bl7 && bl5) {
            this.mc.player.setSprinting(true);
        }

        if ((!this.mc.player.isTouchingWater() || this.mc.player.isSubmergedInWater()) && bl5) {
            this.mc.player.setSprinting(true);
        }

        if (this.mc.player.isSprinting()) {
            boolean bl8 = !this.mc.player.input.hasForwardMovement() || !this.canSprint();
            boolean bl9 = bl8 || this.mc.player.horizontalCollision && !this.mc.player.collidedSoftly || this.mc.player.isTouchingWater() && !this.mc.player.isSubmergedInWater();
            if (this.mc.player.isSwimming()) {
                if (!this.mc.player.isOnGround() && !this.mc.player.input.sneaking && bl8 || !this.mc.player.isTouchingWater()) {
                    this.mc.player.setSprinting(false);
                }
            } else if (bl9) {
                this.mc.player.setSprinting(false);
            }
        }
    }

    private boolean canStartSprinting() {
        return !this.mc.player.isSprinting()
                && this.isWalking()
                && this.canSprint()
                && !this.mc.player.isUsingItem()
                && !this.mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                && (!this.mc.player.hasVehicle() || this.canVehicleSprint(this.mc.player.getVehicle()))
                && !this.mc.player.isFallFlying();
    }

    private boolean canSprint() {
        return this.mc.player.hasVehicle() || (float) this.mc.player.getHungerManager().getFoodLevel() > 6.0F || this.mc.player.getAbilities().allowFlying;
    }

    private boolean isWalking() {
        return this.mc.player.isSubmergedInWater() ? this.mc.player.input.hasForwardMovement() : (double) this.mc.player.input.movementForward >= 0.8;
    }

    private boolean canVehicleSprint(Entity vehicle) {
        return vehicle.canSprintAsVehicle() && vehicle.isLogicalSideForUpdatingMovement();
    }

}
