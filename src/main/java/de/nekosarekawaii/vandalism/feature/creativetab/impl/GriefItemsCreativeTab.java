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

package de.nekosarekawaii.vandalism.feature.creativetab.impl;

import de.nekosarekawaii.vandalism.feature.creativetab.AbstractCreativeTab;
import de.nekosarekawaii.vandalism.util.game.ItemStackUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import static de.nekosarekawaii.vandalism.util.game.ItemStackUtil.withClientSide;

public class GriefItemsCreativeTab extends AbstractCreativeTab {

    public GriefItemsCreativeTab() {
        super(Text.literal("Grief Items"), Items.TNT);
    }

    @Override
    public void exposeItems(final List<ItemStack> items) {
        items.add(withClientSide(createGriefCreeper(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Grief Creeper")));
        items.add(withClientSide(createPoweredGriefCreeper(), Text.literal(Formatting.GOLD + Formatting.BOLD.toString() + "Powered " + Formatting.RED + Formatting.BOLD + "Grief Creeper"), true));
        items.add(withClientSide(ItemStackUtil.createSpawnEggItemStack((SpawnEggItem) Items.SHEEP_SPAWN_EGG, "minecraft:ender_dragon"), Text.literal(Formatting.LIGHT_PURPLE + Formatting.BOLD.toString() + "Ender Dragon")));
        items.add(withClientSide(ItemStackUtil.createSpawnEggItemStack((SpawnEggItem) Items.COW_SPAWN_EGG, "minecraft:wither"), Text.literal(Formatting.WHITE + Formatting.BOLD.toString() + "Wither")));
    }

    private static ItemStack createGriefCreeper() {
        final ItemStack item = new ItemStack(Items.PIG_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        entityTag.putInt("Fuse", 0);
        entityTag.putInt("ExplosionRadius", 127);
        entityTag.putString("id", "minecraft:creeper");
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createPoweredGriefCreeper() {
        final ItemStack item = new ItemStack(Items.COW_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        entityTag.putInt("Fuse", 0);
        entityTag.putInt("ExplosionRadius", 127);
        entityTag.putByte("powered", (byte) 1);
        entityTag.putString("id", "minecraft:creeper");
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

}
