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

package de.nekosarekawaii.vandalism.feature.creativetab;

import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public abstract class CreativeTab {

    private final List<ItemStack> TEMP_ITEMS = new ArrayList<>();

    private final Text name;
    private final ItemStack icon;

    public CreativeTab(final Text name, final Item icon) {
        this.name = name;
        this.icon = new ItemStack(icon);
    }

    public abstract void exposeItems(final List<ItemStack> items);

    public void publish() {
        final ItemGroup itemGroup = FabricItemGroup.builder().icon(() -> this.icon).displayName(this.name).entries(((displayContext, entries) -> {
            if (this.TEMP_ITEMS.isEmpty()) {
                exposeItems(this.TEMP_ITEMS);
            }
            entries.addAll(this.TEMP_ITEMS);
        })).build();
        Registry.register(Registries.ITEM_GROUP, Identifier.of(
                FabricBootstrap.MOD_ID,
                this.name.getString().toLowerCase().replace(" ", "_")
        ), itemGroup);
    }

}
