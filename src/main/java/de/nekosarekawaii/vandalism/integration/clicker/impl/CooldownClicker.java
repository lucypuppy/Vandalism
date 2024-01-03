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

package de.nekosarekawaii.vandalism.integration.clicker.impl;

import de.nekosarekawaii.vandalism.integration.clicker.Clicker;
import de.nekosarekawaii.vandalism.util.minecraft.TimerHack;
import net.minecraft.entity.attribute.EntityAttributes;

public class CooldownClicker extends Clicker {

    @Override
    public void onUpdate() {
        final float baseAttackDamage = (float) mc.player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);

        float additionalBaseDelayOffset = 0;

        if (TimerHack.getSpeed() > 1) {
            additionalBaseDelayOffset = -(TimerHack.getSpeed() - 1);
        }

        final float attackCooldown = mc.player.getAttackCooldownProgress(additionalBaseDelayOffset);
        final float finalAttackDamage = baseAttackDamage * (0.2F + attackCooldown * attackCooldown * 0.8F);

        if (finalAttackDamage >= 0.98) {
            this.clickAction.accept(true);
            this.mc.player.resetLastAttackedTicks();
        } else {
            this.clickAction.accept(false);
        }
    }


}
