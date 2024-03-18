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

package de.nekosarekawaii.vandalism.feature.module.impl.render;

import de.nekosarekawaii.vandalism.base.value.impl.minecraft.MultiRegistryBlacklistValue;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.game.WorldUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

import java.util.Collections;

public class ESPModule extends AbstractModule {

    private final BooleanValue items = new BooleanValue(
            this,
            "Items",
            "Whether items should also have an ESP.",
            false
    );

    private final MultiRegistryBlacklistValue<Item> itemList = new MultiRegistryBlacklistValue<>(
            this,
            "Item List",
            "The items to target.",
            Registries.ITEM,
            Collections.singletonList(
                    Items.AIR
            )
    ).visibleCondition(this.items::getValue);

    public final ColorValue outlineColor = new ColorValue(
            this,
            "Color",
            "The color of the outline."
    );

    public ESPModule() {
        super(
                "ESP",
                "Lets you see blocks or entities trough blocks.",
                Category.RENDER
        );
    }

    public boolean isTarget(final Entity entity) {
        return WorldUtil.isTarget(entity) || entity instanceof final ItemEntity itemEntity && this.itemList.isSelected(itemEntity.getStack().getItem()) && this.items.getValue();
    }

}
