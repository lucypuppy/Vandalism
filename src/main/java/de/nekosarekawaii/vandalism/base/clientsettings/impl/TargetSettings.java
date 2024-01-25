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
import de.nekosarekawaii.vandalism.base.value.impl.minecraft.MultiRegistryValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;

import java.util.Collections;

public class TargetSettings extends ValueGroup implements TargetListener {

    private final MultiRegistryValue<EntityType<?>> targets = new MultiRegistryValue<>(
            this,
            "Targets",
            "The entities to target.",
            Registries.ENTITY_TYPE,
            Collections.singletonList(EntityType.PLAYER)
    );

    public TargetSettings(final ClientSettings parent) {
        super(parent, "Target", "Target related settings.");
        Vandalism.getInstance().getEventSystem().subscribe(TargetEvent.ID, this, Priorities.HIGHEST);
    }

    @Override
    public void onTarget(final TargetEvent event) {
        final Entity entity = event.entity;
        if (entity == this.mc.player) {
            event.isTarget = false;
            return;
        }
        if (!this.targets.isSelected(entity.getType())) {
            event.isTarget = false;
        }
    }

}
