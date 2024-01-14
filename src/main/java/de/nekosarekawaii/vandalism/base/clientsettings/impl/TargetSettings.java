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

package de.nekosarekawaii.vandalism.base.clientsettings.impl;

import de.florianmichael.dietrichevents2.Priorities;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.ClientSettings;
import de.nekosarekawaii.vandalism.base.event.normal.internal.TargetListener;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class TargetSettings extends ValueGroup implements TargetListener {

    private final Value<Boolean> players = new BooleanValue(
            this,
            "Players",
            "Whether players should be attacked.",
            true
    );

    private final Value<Boolean> hostile = new BooleanValue(
            this,
            "Hostile",
            "Whether hostile mobs should be attacked.",
            false
    );

    private final Value<Boolean> animals = new BooleanValue(
            this,
            "Animals",
            "Whether animals should be attacked.",
            false
    );

    private final Value<Boolean> villager = new BooleanValue(
            this,
            "Villager",
            "Whether villager should be attacked",
            false
    );

    private final Value<Boolean> isAlive = new BooleanValue(
            this,
            "Alive",
            "Checks if the entity is alive.",
            true
    );

    public TargetSettings(final ClientSettings parent) {
        super(parent, "Target", "Target related settings.");
        Vandalism.getInstance().getEventSystem().subscribe(TargetEvent.ID, this, Priorities.HIGHEST);
    }

    @Override
    public void onTarget(TargetEvent event) {
        if (!(event.entity instanceof final LivingEntity livingEntity
                && livingEntity != mc.player
                && (livingEntity.isAlive() || !isAlive.getValue()) &&
                ((livingEntity instanceof PlayerEntity && players.getValue())
                        || (livingEntity instanceof HostileEntity && hostile.getValue())
                        || (livingEntity instanceof AnimalEntity && animals.getValue())
                        || (livingEntity instanceof VillagerEntity && villager.getValue())))) {
            event.isTarget = false;
        }
    }
}
