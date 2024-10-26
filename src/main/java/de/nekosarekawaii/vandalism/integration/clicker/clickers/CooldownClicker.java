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

package de.nekosarekawaii.vandalism.integration.clicker.clickers;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.template.module.ClickerModule;
import de.nekosarekawaii.vandalism.integration.clicker.Clicker;
import net.minecraft.entity.attribute.EntityAttributes;

import static de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper.mc;

public class CooldownClicker extends Clicker implements PlayerUpdateListener {

    private final IntegerValue minDamagePercentage = new IntegerValue(
            this,
            "Min damage percentage",
            "Min percent of max damage for click.",
            100,
            1,
            100
    );

    public CooldownClicker(final ClickerModule clickerModule) {
        super(clickerModule, "Cooldown");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (!this.clickerModule.mode.isSelected(this) || !clickerModule.shouldClick()) {
            return;
        }

        final float baseAttackDamage = (float) mc.player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        final float attackCooldown = mc.player.getAttackCooldownProgress(0);
        final float finalAttackDamage = baseAttackDamage * (0.2f + attackCooldown * attackCooldown * 0.8f);
        if (finalAttackDamage >= this.minDamagePercentage.getValue() * 0.01) {
            this.clickerModule.onClick();
        }
    }

}
