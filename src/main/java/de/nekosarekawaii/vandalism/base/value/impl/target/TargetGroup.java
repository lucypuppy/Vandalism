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

package de.nekosarekawaii.vandalism.base.value.impl.target;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.impl.minecraft.MultiRegistryBlacklistValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.event.internal.TargetListener;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;

import java.util.Arrays;
import java.util.List;

public class TargetGroup extends ValueGroup {

    private static final List<EntityType<?>> BLACKLISTED_ENTITY_TYPES = Arrays.asList(
            EntityType.ITEM,
            EntityType.EXPERIENCE_ORB,
            EntityType.AREA_EFFECT_CLOUD,
            EntityType.FISHING_BOBBER,
            EntityType.LIGHTNING_BOLT,
            EntityType.ARROW,
            EntityType.SPECTRAL_ARROW,
            EntityType.TRIDENT,
            EntityType.EGG,
            EntityType.POTION,
            EntityType.SNOWBALL,
            EntityType.ENDER_PEARL,
            EntityType.EXPERIENCE_BOTTLE,
            EntityType.BLOCK_DISPLAY,
            EntityType.TEXT_DISPLAY,
            EntityType.ITEM_DISPLAY,
            EntityType.INTERACTION,
            EntityType.MARKER,
            EntityType.FALLING_BLOCK,
            EntityType.EVOKER_FANGS,
            EntityType.WITHER_SKULL,
            EntityType.FIREWORK_ROCKET,
            EntityType.TNT,
            EntityType.LLAMA_SPIT,
            EntityType.EYE_OF_ENDER
    );

    @Getter
    private final MultiRegistryBlacklistValue<EntityType<?>> targetListValue = new MultiRegistryBlacklistValue<>(
            this,
            "Targets",
            "The entities to target.",
            Registries.ENTITY_TYPE,
            BLACKLISTED_ENTITY_TYPES,
            false
    );

    private final BooleanValue isAlive = new BooleanValue(
            this,
            "Alive",
            "Whether the target has to be alive or not.",
            true
    );

    public TargetGroup(final ValueParent parent, final String name, final String description) {
        super(parent, name, description);
    }

    public boolean isTarget(final Entity entity) {
        return this.isTarget(entity, false);
    }

    public boolean isTarget(final Entity entity, final boolean ignoreFriends) {
        if (entity == this.mc.player || (!entity.isAlive() && this.isAlive.getValue())) {
            return false;
        }
        if (!this.targetListValue.isSelected(entity.getType())) {
            return false;
        }
        final TargetListener.TargetEvent event = new TargetListener.TargetEvent(entity, ignoreFriends);
        Vandalism.getInstance().getEventSystem().callExceptionally(TargetListener.TargetEvent.ID, event);
        return event.isTarget;
    }

}
