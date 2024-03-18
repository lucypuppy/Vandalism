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

package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.normal.player.AttackListener;
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.integration.newrotation.RotationUtil;
import de.nekosarekawaii.vandalism.util.game.MovementUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class WTapModule extends AbstractModule implements AttackListener, PlayerUpdateListener {

    private Entity movementTarget = null;
    private LivingEntity lastTarget = null;

    public WTapModule() {
        super(
                "W Tap",
                "Automatically sprints and un-sprints when you are in combat which applies more velocity to your target.",
                Category.COMBAT
        );
    }

    private void reset() {
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
        if (this.mc.player == null) {
            return;
        }
        if (event.target instanceof final LivingEntity livingEntity && this.movementTarget == null) {
            this.lastTarget = livingEntity;
            final boolean isLooking = RotationUtil.isEntityLookingAtEntity(this.mc.player, livingEntity, 80);

            if (MovementUtil.isMoving() && (livingEntity.hurtTime <= 2 || livingEntity.hurtTime == 9) && isLooking) {
                this.mc.options.forwardKey.setPressed(false);
                this.movementTarget = livingEntity;
            }
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.movementTarget != null) {
            final boolean isLooking = RotationUtil.isEntityLookingAtEntity(this.mc.player, this.movementTarget, 80);
            final double speed = MovementUtil.getSpeedRelatedToYaw(this.mc.player.getYaw());

            if (speed < 0.3D || !isLooking) {
                this.mc.options.forwardKey.setPressed(true);
                this.movementTarget = null;
            }

            if (this.lastTarget == null) {
                return;
            }

            if (this.lastTarget.hurtTime == 10 || this.lastTarget.hurtTime == 8) {
                this.mc.options.forwardKey.setPressed(true);
                if (this.lastTarget.hurtTime == 8) {
                    this.lastTarget = null;
                }
            }
        }
    }

}
